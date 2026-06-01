package org.hang.shortlink.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.propreties.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void initSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getTtl()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Token不能为空");
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期：{}", token);
            throw new RuntimeException("Token已过期", e);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.error("无效的Token：{}", token, e);
            throw new RuntimeException("无效的Token：签名不匹配/格式错误", e);
        } catch (IllegalArgumentException e) {
            log.error("Token解析参数错误：{}", token, e);
            throw new RuntimeException("Token解析失败：参数不合法", e);
        }
    }

    public String getTokenName() {
        return jwtProperties.getTokenName();
    }

    public long getTtl() {
        return jwtProperties.getTtl();
    }
}
