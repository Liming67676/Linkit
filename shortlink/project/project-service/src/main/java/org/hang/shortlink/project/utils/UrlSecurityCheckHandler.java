package org.hang.shortlink.project.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.propreties.UrlCheckProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * URL安全检测（优化版）
 * 白名单/黑名单存储于Redis，支持动态维护
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(UrlCheckProperties.class)
public class UrlSecurityCheckHandler {

    // ====================== 缓存配置 ======================
    private static final String URL_CHECK_CACHE_PREFIX = "short-link:url:check:";
    private static final String STATUS_SAFE = "1";
    private static final String STATUS_UNSAFE = "0";

    // ====================== Redis 黑白名单常量Key ======================
    private static final String REDIS_WHITELIST_KEY = "short-link:url:whitelist:domains";
    private static final String REDIS_BLACKLIST_KEY = "short-link:url:blacklist:domains";

    // ====================== 腾讯云TMS官方固定参数 ======================
    private static final String ENDPOINT = "tms.tencentcloudapi.com";
    private static final String ACTION = "TextModeration";
    private static final String VERSION = "2020-12-29";
    private static final String SERVICE = "tms";
    private static final String REGION = "ap-guangzhou";
    private static final long SLOW_TMS_CHECK_MILLIS = 800L;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    private final UrlCheckProperties urlCheckProperties;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    /**
     * 项目启动初始化：将YAML配置的黑白名单同步到Redis
     * 后续直接修改Redis即可，无需改配置重启
     */
    @PostConstruct
    public void initDomainListToRedis() {
        try {
            // 同步白名单到Redis
            List<String> whitelist = urlCheckProperties.getWhitelistDomains();
            if (whitelist != null && !whitelist.isEmpty()) {
                redisTemplate.opsForSet().add(REDIS_WHITELIST_KEY, whitelist.toArray(new String[0]));
                log.info("[URL安全校验] 初始化白名单到Redis完成，数量：{}", whitelist.size());
            }

            // 同步黑名单到Redis
            List<String> blacklist = urlCheckProperties.getBlacklistDomains();
            if (blacklist != null && !blacklist.isEmpty()) {
                redisTemplate.opsForSet().add(REDIS_BLACKLIST_KEY, blacklist.toArray(new String[0]));
                log.info("[URL安全校验] 初始化黑名单到Redis完成，数量：{}", blacklist.size());
            }
        } catch (Exception e) {
            log.error("[URL安全校验] 初始化Redis黑白名单失败", e);
        }
    }

    public void checkUrl(String originUrl) {
        log.info("[URL安全校验] 开始校验链接：{}", originUrl);
        if (!urlCheckProperties.isEnabled()) {
            return;
        }

        // 基础参数校验
        if (StrUtil.isBlank(originUrl)) {
            throw new RuntimeException("目标链接不能为空");
        }
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            throw new RuntimeException("仅支持 HTTP/HTTPS 协议的链接");
        }

        // 缓存校验：优先读取Redis缓存结果
        String cacheKey = URL_CHECK_CACHE_PREFIX + SecureUtil.md5(originUrl);
        String cacheResult = redisTemplate.opsForValue().get(cacheKey);
        if (STATUS_UNSAFE.equals(cacheResult)) {
            throw new RuntimeException("该链接已被标记为不安全，无法创建短链接");
        }
        if (STATUS_SAFE.equals(cacheResult)) {
            return;
        }

        try {
            // 解析域名
            URI uri = new URI(originUrl);
            String domain = uri.getHost();
            if (StrUtil.isBlank(domain)) {
                throw new RuntimeException("无法解析链接域名");
            }
            String processDomain = domain.replace("www.", "");

            // 逻辑：白名单放行 → 黑名单拦截
            // 1. 白名单校验
            if (urlCheckProperties.isWhitelistEnabled()) {
                boolean isInWhitelist = isDomainInWhitelist(processDomain);
                if (isInWhitelist) {
                    redisTemplate.opsForValue().set(cacheKey, STATUS_SAFE, urlCheckProperties.getCacheSeconds(), TimeUnit.SECONDS);
                    return;
                }
            }

            // 2. 黑名单校验
            boolean isInBlacklist = isDomainInBlacklist(processDomain);
            if (isInBlacklist) {
                redisTemplate.opsForValue().set(cacheKey, STATUS_UNSAFE, urlCheckProperties.getCacheSeconds(), TimeUnit.SECONDS);
                throw new RuntimeException("该域名已被加入黑名单，禁止创建短链接");
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("[URL安全校验] 链接解析/校验异常，url：{}", originUrl, e);
            throw new RuntimeException("链接校验失败，请检查链接格式后重试");
        }
    }

    public boolean checkUrlByTencentTms(String originUrl) {
        String cacheKey = URL_CHECK_CACHE_PREFIX + SecureUtil.md5(originUrl);
        String cacheResult = redisTemplate.opsForValue().get(cacheKey);
        if (STATUS_UNSAFE.equals(cacheResult)) {
            return false;
        }
        if (STATUS_SAFE.equals(cacheResult)) {
            return true;
        }
        String checkResult = checkByTencentTms(originUrl);
        redisTemplate.opsForValue().set(cacheKey, checkResult, urlCheckProperties.getCacheSeconds(), TimeUnit.SECONDS);
        return !STATUS_UNSAFE.equals(checkResult);
    }

    /**
     * 判断域名是否在 Redis 白名单中
     */
    private boolean isDomainInWhitelist(String processDomain) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(REDIS_WHITELIST_KEY, processDomain));
    }

    /**
     * 判断域名是否在 Redis 黑名单中
     */
    private boolean isDomainInBlacklist(String processDomain) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(REDIS_BLACKLIST_KEY, processDomain));
    }

    /**
     * 腾讯云TMS文本检测（严格对齐官方文档，无修改）
     */
    private String checkByTencentTms(String url) {
        long startTime = System.currentTimeMillis();
        UrlCheckProperties.TencentCms cmsConfig = urlCheckProperties.getTencentCms();
        if (cmsConfig == null || StrUtil.isBlank(cmsConfig.getSecretId())
                || StrUtil.isBlank(cmsConfig.getSecretKey())
                || StrUtil.isBlank(cmsConfig.getBizType())) {
            log.warn("[腾讯云TMS] 配置缺失，跳过检测");
            return STATUS_SAFE;
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            String date = DATE_FORMATTER.format(Instant.ofEpochSecond(timestamp));

            // 官方要求：Content必须Base64编码
            String base64Content = Base64.getEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
            JSONObject body = JSONUtil.createObj()
                    .set("Content", base64Content)
                    .set("BizType", cmsConfig.getBizType());
            String bodyJson = JSONUtil.toJsonStr(body);
            String bodyHash = sha256Hex(bodyJson);

            // 官方标准签名构建
            String canonicalRequest = "POST\n" +
                    "/\n" +
                    "\n" +
                    "content-type:application/json\n" +
                    "host:" + ENDPOINT + "\n" +
                    "\n" +
                    "content-type;host\n" +
                    bodyHash;

            String credentialScope = date + "/" + SERVICE + "/tc3_request";
            String stringToSign = "TC3-HMAC-SHA256\n" +
                    timestamp + "\n" +
                    credentialScope + "\n" +
                    sha256Hex(canonicalRequest);

            String signature = sign(cmsConfig.getSecretKey(), date, stringToSign);
            String auth = "TC3-HMAC-SHA256 Credential=" + cmsConfig.getSecretId() + "/" + credentialScope
                    + ", SignedHeaders=content-type;host, Signature=" + signature;

            // 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Host", ENDPOINT);
            headers.set("X-TC-Action", ACTION);
            headers.set("X-TC-Version", VERSION);
            headers.set("X-TC-Region", REGION);
            headers.set("X-TC-Timestamp", String.valueOf(timestamp));
            headers.set("Authorization", auth);

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://" + ENDPOINT, entity, String.class);
            logSlowTmsCheck(url, startTime);

            if (response.getBody() == null) {
                return STATUS_SAFE;
            }

            JSONObject resp = JSONUtil.parseObj(response.getBody());
            JSONObject responseData = resp.getJSONObject("Response");

            if (responseData != null && responseData.containsKey("Error")) {
                log.error("[腾讯云TMS] 接口错误：{}", responseData.get("Error"));
                return STATUS_SAFE;
            }

            // 官方返回规则：Block=违规
            String suggestion = responseData.getStr("Suggestion");
            if ("Block".equals(suggestion)) {
                log.warn("[腾讯云TMS] 违规链接：{}，标签：{}", url, responseData.getStr("Label"));
                return STATUS_UNSAFE;
            }

            return STATUS_SAFE;

        } catch (Exception e) {
            log.error("[腾讯云TMS] 调用失败, url={}, costMs={}", url, System.currentTimeMillis() - startTime, e);
            return STATUS_SAFE;
        }
    }

    private void logSlowTmsCheck(String url, long startTime) {
        long costMs = System.currentTimeMillis() - startTime;
        if (costMs >= SLOW_TMS_CHECK_MILLIS) {
            log.info("[腾讯云TMS] 检测耗时较长, url={}, costMs={}", url, costMs);
        }
    }

    /**
     * 腾讯云TC3签名算法（官方标准）
     */
    private String sign(String secretKey, String date, String stringToSign) throws Exception {
        byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmacSha256(secretDate, SERVICE);
        byte[] secretSigning = hmacSha256(secretService, "tc3_request");
        byte[] signature = hmacSha256(secretSigning, stringToSign);
        return encodeHex(signature);
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String sha256Hex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return encodeHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("SHA256计算失败", e);
        }
    }

    private String encodeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
