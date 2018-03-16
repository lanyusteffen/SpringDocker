package stu.lanyu.springdocker.redis;

import io.lettuce.core.Range;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import stu.lanyu.springdocker.config.GlobalConfig;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RedisRateLimiter {

    /**
     * 分布式令牌桶算法实现限流
     * @param connection Redis连接实例
     * @param method 限流方法名
     * @param qps 单位时间间隔内允许放入的令牌数量
     * @param interval 单位时间间隔, 单位:毫秒
     * @param minTimeSinceLastRequest 最小令盘获取时间差, 单位:毫秒
     * @return
     */
    public static boolean acquireToken(StatefulRedisConnection<String, String> connection, String method, int qps, int interval, int minTimeSinceLastRequest) {
        long now = System.currentTimeMillis();

        RedisCommands<String, String> transaction = connection.sync();

        long clearBefore = now - interval;

        String id = GlobalConfig.RateLimiter.TOKEN_BUCKET_IDENTITIEFER + method;

        // 移除已超时的令牌
        transaction.zremrangebyscore(id, Range.create(0, clearBefore));
        transaction.zrange(id, 0, -1);
        transaction.zadd(id, now, UUID.randomUUID().toString());

        // 设置令牌超时过期时间 双保险
        transaction.expire(id, (int)interval / 1000);

        TransactionResult results = transaction.exec();

        List<Long> timestamps = IntStream.range(0, results.size())
                        .filter(i -> i % 2 == 0)
                        .mapToObj(results::get)
                        .map(x -> Long.valueOf(x.toString()))
                        .collect(Collectors.toList());

        // qps超标
        boolean tooManyInInterval = (timestamps.size() > qps);

        if (tooManyInInterval)
            return false;

        long timeSinceLastRequest = now - timestamps.get(timestamps.size() - 1);

        boolean tooBusyInInterval = (timeSinceLastRequest <= minTimeSinceLastRequest);

        if (tooBusyInInterval)
            return false;

        return true;
    }
}
