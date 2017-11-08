package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import redis.clients.jedis.JedisPoolConfig;
import stu.lanyu.springdocker.message.HeartbeatMessageReceiver;
import stu.lanyu.springdocker.message.LogCollectMessageReceiver;
import stu.lanyu.springdocker.message.RegisterMessageReceiver;
import stu.lanyu.springdocker.message.WarningMessageReceiver;
import stu.lanyu.springdocker.schedule.HeartbeatSchedule;

@Configuration
@EnableConfigurationProperties(RedisMessageProperties.class)
public class RedisMessageConfig {

    @Autowired(required = false)
    private RedisMessageProperties redisMessageProperties;

    private JedisConnectionFactory getRedisMessageConnectionFactory(){

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisMessageProperties.getHost());
        jedisConnectionFactory.setPort(redisMessageProperties.getPort());
        jedisConnectionFactory.setTimeout(redisMessageProperties.getTimeOut());
        jedisConnectionFactory.setUsePool(true);

        jedisConnectionFactory.setPassword(redisMessageProperties.getPassword());

        JedisPoolConfig poolConfig = jedisConnectionFactory.getPoolConfig();
        poolConfig.setMaxIdle(redisMessageProperties.getMaxIdle());
        poolConfig.setMinIdle(redisMessageProperties.getMinIdle());
        poolConfig.setMaxTotal(redisMessageProperties.getMaxTotal());
        poolConfig.setMaxWaitMillis(redisMessageProperties.getMaxWaitMillis());

        jedisConnectionFactory.setPoolConfig(poolConfig);

        jedisConnectionFactory.afterPropertiesSet();

        return jedisConnectionFactory;
    }

    @Bean
    HeartbeatSchedule getHeartbeatSchedule() {
        return new HeartbeatSchedule();
    }

    @Bean
    RegisterMessageReceiver getRegisterMessageReceiver() {
        return new RegisterMessageReceiver();
    }

    @Bean
    WarningMessageReceiver getWarningMessageReceiver() {
        return new WarningMessageReceiver();
    }

    @Bean
    LogCollectMessageReceiver getLogCollectMessageReceiver() {
        return new LogCollectMessageReceiver();
    }

    @Bean
    HeartbeatMessageReceiver getHeartbeatMessageReceiver() {
        return new HeartbeatMessageReceiver();
    }

    @Bean
    RedisMessageListenerContainer redisContainer(WarningMessageReceiver warningMessageReceiver, LogCollectMessageReceiver logCollectMessageReceiver, HeartbeatMessageReceiver heartbeatMessageReceiver, RegisterMessageReceiver registerMessageReceiver) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(getRedisMessageConnectionFactory());

        container.addMessageListener(logCollectMessageReceiver,
                new ChannelTopic(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL));

        container.addMessageListener(warningMessageReceiver,
                new ChannelTopic(GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL));

        container.addMessageListener(heartbeatMessageReceiver,
                new ChannelTopic(GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL));

        container.addMessageListener(registerMessageReceiver,
                new ChannelTopic(GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL));

        return container;
    }
}
