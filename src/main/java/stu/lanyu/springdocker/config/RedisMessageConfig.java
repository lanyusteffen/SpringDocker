package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.HeartbeatSubscriber;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.RegisterSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.schedule.HeartbeatSchedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//import stu.lanyu.springdocker.message.receiver.HeartbeatMessageReceiver;
//import stu.lanyu.springdocker.message.receiver.LogCollectMessageReceiver;
//import stu.lanyu.springdocker.message.receiver.RegisterMessageReceiver;
//import stu.lanyu.springdocker.message.receiver.WarningMessageReceiver;

@Configuration
@EnableConfigurationProperties(RedisMessageProperties.class)
public class RedisMessageConfig {

    @Autowired(required = true)
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

    private JedisPool getRedisMessagePool() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxIdle(redisMessageProperties.getMaxIdle());
        poolConfig.setMinIdle(redisMessageProperties.getMinIdle());
        poolConfig.setMaxTotal(redisMessageProperties.getMaxTotal());
        poolConfig.setMaxWaitMillis(redisMessageProperties.getMaxWaitMillis());

        JedisPool jedisPool = new JedisPool(poolConfig, redisMessageProperties.getHost(), redisMessageProperties.getPort(),
                redisMessageProperties.getTimeOut(), redisMessageProperties.getPassword());

        return jedisPool;
    }

    @Bean
    HeartbeatSubscriber getHeartbeatSubscriber() {
        return new HeartbeatSubscriber();
    }

    @Bean
    LogCollectSubscriber getLogCollectSubscriber() {
        return new LogCollectSubscriber();
    }

    @Bean
    RegisterSubscriber getRegisterSubscriber() {
        return new RegisterSubscriber();
    }

    @Bean
    WarningSubscriber getWarningSubscriber() {
        return new WarningSubscriber();
    }

    @Bean(name = "HeartbeatExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getHeartbeatScheduleExecutorService(HeartbeatSubscriber heartbeatSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {
            try {
                JedisPool pool = getRedisMessagePool();
                Jedis jedis = pool.getResource();
                jedis.subscribe(heartbeatSubscriber, GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
    }

    @Bean(name = "LogCollectExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getLogCollectScheduleExecutorService(LogCollectSubscriber logCollectSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {
            try {
                JedisPool pool = getRedisMessagePool();
                Jedis jedis = pool.getResource();
                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
    }

    @Bean(name = "WarningExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getWarningScheduleExecutorService(WarningSubscriber warningSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            try {
                JedisPool pool = getRedisMessagePool();
                Jedis jedis = pool.getResource();
                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
    }

    @Bean(name = "RegisterExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getRegisterScheduleExecutorService(RegisterSubscriber registerSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            try {
                JedisPool pool = getRedisMessagePool();
                Jedis jedis = pool.getResource();
                jedis.subscribe(registerSubscriber, GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
    }

    @Bean
    HeartbeatSchedule getHeartbeatSchedule() {
        return new HeartbeatSchedule();
    }

//    @Bean
//    RegisterMessageReceiver getRegisterMessageReceiver() {
//        return new RegisterMessageReceiver();
//    }
//
//    @Bean
//    WarningMessageReceiver getWarningMessageReceiver() {
//        return new WarningMessageReceiver();
//    }
//
//    @Bean
//    LogCollectMessageReceiver getLogCollectMessageReceiver() {
//        return new LogCollectMessageReceiver();
//    }
//
//    @Bean
//    HeartbeatMessageReceiver getHeartbeatMessageReceiver() {
//        return new HeartbeatMessageReceiver();
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisLogCollectContainer(LogCollectMessageReceiver logCollectMessageReceiver) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//        container.setConnectionFactory(getRedisMessageConnectionFactory());
//
//        container.addMessageListener(new MessageListenerAdapter(logCollectMessageReceiver),
//                new ChannelTopic(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL));
//
//        return container;
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisWarningContainer(WarningMessageReceiver warningMessageReceiver) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//        container.setConnectionFactory(getRedisMessageConnectionFactory());
//
//        container.addMessageListener(new MessageListenerAdapter(warningMessageReceiver),
//                new ChannelTopic(GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL));
//
//        return container;
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisRegisterContainer(RegisterMessageReceiver registerMessageReceiver) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//        container.setConnectionFactory(getRedisMessageConnectionFactory());
//
//        container.addMessageListener(new MessageListenerAdapter(registerMessageReceiver),
//                new ChannelTopic(GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL));
//
//        return container;
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisHeartbeatContainer(HeartbeatMessageReceiver heartbeatMessageReceiver) {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//
//        container.setConnectionFactory(getRedisMessageConnectionFactory());
//
//        container.addMessageListener(new MessageListenerAdapter(heartbeatMessageReceiver),
//                new ChannelTopic(GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL));
//
//        return container;
//    }
}
