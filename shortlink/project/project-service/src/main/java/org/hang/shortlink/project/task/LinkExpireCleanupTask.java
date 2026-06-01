package org.hang.shortlink.project.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.constant.RedisConstant;
import org.hang.shortlink.project.mapper.LinkMapper;
import org.hang.shortlink.project.pojo.po.Link;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 短链接过期清理定时任务
 * 多实例部署下，仅一个节点执行，无重复执行问题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkExpireCleanupTask {

    private final LinkMapper linkMapper;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    // 单gid单次批量处理数量
    private static final int BATCH_SIZE = 1000;
    // 分布式锁
    private static final String LOCK_KEY = "short-link:expire-clean:lock";
    // 锁超时10分钟
    private static final long LOCK_EXPIRE = 10L;

    /**
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredLinks() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        boolean lockSuccess = false;

        try {
            // 分布式锁：抢不到直接跳过
            lockSuccess = lock.tryLock(0, LOCK_EXPIRE, TimeUnit.MINUTES);
            if (!lockSuccess) {
                log.info("[过期清理] 其他实例已执行，本实例跳过");
                return;
            }
            log.info("[过期清理] 分布式锁抢占成功，开始处理gid分表过期链接");

            LocalDateTime now = LocalDateTime.now();
            int totalClean = 0;


            // 1：查询所有有效gid（分片键），适配分表
            List<String> gidList = linkMapper.selectDistinctGid();
            if (gidList.isEmpty()) {
                log.info("[过期清理] 无有效分组，任务结束");
                return;
            }

            // 2：遍历每个gid，单独处理对应分表（精准路由，无广播）
            for (String gid : gidList) {
                try {
                    int pageNo = 1;
                    while (true) {
                        // 精准查询当前gid分表的过期数据
                        Page<Link> page = new Page<>(pageNo, BATCH_SIZE);
                        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper
                                .eq(Link::getGid, gid) // 核心：指定分片键gid
                                .eq(Link::getEnableStatus, 0)
                                .eq(Link::getDelFlag, 0)
                                .lt(Link::getValidDate, now)
                                .orderByAsc(Link::getId);

                        Page<Link> linkPage = linkMapper.selectPage(page, queryWrapper);
                        List<Link> expiredLinks = linkPage.getRecords();
                        if (expiredLinks.isEmpty()) break;

                        // 精准更新当前gid分表
                        LambdaUpdateWrapper<Link> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper
                                .eq(Link::getGid, gid) // 指定分片键gid
                                .in(Link::getId, expiredLinks.stream().map(Link::getId).toList())
                                .set(Link::getEnableStatus, 1)
                                .set(Link::getDelFlag, 1);

                        linkMapper.update(null, updateWrapper);
                        totalClean += expiredLinks.size();
                        redisTemplate.delete(String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid));
                        pageNo++;
                    }
                } catch (Exception e) {
                    log.error("[过期清理] 处理gid:{} 失败", gid, e);
                }
            }

            log.info("[过期清理] 任务完成，分表模式下共清理过期链接：{} 条", totalClean);

        } catch (InterruptedException e) {
            log.error("[过期清理] 任务中断", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[过期清理] 任务执行失败", e);
        } finally {
            if (lockSuccess && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[过期清理] 分布式锁已释放");
            }
        }
    }
}
