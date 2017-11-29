package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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
import stu.lanyu.springdocker.schedule.MaintainSchedule;

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

    @Bean("RedisSubscriberConnectionFactory")
    @Scope("singleton")
    JedisConnectionFactory getRedisMessageConnectionFactory(){

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
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestOnBorrow(true);

        jedisConnectionFactory.setPoolConfig(poolConfig);

        jedisConnectionFactory.afterPropertiesSet();

        return jedisConnectionFactory;
    }

    @Bean("RedisSubscriberMessagePool")
    @Scope("singleton")
    JedisPool getRedisMessagePool() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxIdle(redisMessageProperties.getMaxIdle());
        poolConfig.setMinIdle(redisMessageProperties.getMinIdle());
        poolConfig.setMaxTotal(redisMessageProperties.getMaxTotal());
        poolConfig.setMaxWaitMillis(redisMessageProperties.getMaxWaitMillis());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

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

            JedisPool pool = context.getBean("RedisSubscriberMessagePool",
                    JedisPool.class);
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(heartbeatSubscriber, GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);
                System.out.println("ESFTask.Commands.ESFTaskHeartbeatChannel end!");
            } catch (Exception e) {
                System.out.println("ESFTask.Commands.ESFTaskHeartbeatChannel error: " + e.getMessage());
            }
            finally {
                if (jedis != null){
                    jedis.quit();
                    jedis.close();
                }
                jedis = null;
            }

            ScheduledExecutorService serviceHost = context.getBean("HeartbeatExecutorService", ScheduledExecutorService.class);
            serviceHost.shutdownNow();
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

            JedisPool pool = context.getBean("RedisSubscriberMessagePool",
                    JedisPool.class);
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
                System.out.println("ESFTask.Commands.ESFTaskPushLogChannel end!");
            } catch (Exception e) {
                System.out.println("ESFTask.Commands.ESFTaskPushLogChannel error: " + e.getMessage());
            }
            finally {
                if (jedis != null){
                    jedis.quit();
                    jedis.close();
                }
                jedis = null;
            }

            ScheduledExecutorService serviceHost = context.getBean("LogCollectExecutorService", ScheduledExecutorService.class);
            serviceHost.shutdownNow();
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

            JedisPool pool = context.getBean("RedisSubscriberMessagePool",
                    JedisPool.class);
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
                System.out.println("ESFTask.Commands.ESFTaskWarningChannel end!");
            } catch (Exception e) {
                System.out.println("ESFTask.Commands.ESFTaskWarningChannel error: " + e.getMessage());
            }
            finally {
                if (jedis != null){
                    jedis.quit();
                    jedis.close();
                }
                jedis = null;
            }

            ScheduledExecutorService serviceHost = context.getBean("WarningExecutorService", ScheduledExecutorService.class);
            serviceHost.shutdownNow();
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
    }

    @Autowired(required = true)
    private ApplicationContext context;

    @Bean(name = "RegisterExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getRegisterScheduleExecutorService(RegisterSubscriber registerSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = context.getBean("RedisSubscriberMessagePool",
                    JedisPool.class);
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(registerSubscriber, GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);
                System.out.println("ESFTask.Commands.ESFTaskRegisterChannel end!");
            } catch (Exception e) {
                System.out.println("ESFTask.Commands.ESFTaskRegisterChannel error: " + e.getMessage());
            }
            finally {
                if (jedis != null){
                    jedis.quit();
                    jedis.close();
                }
                jedis = null;
            }

            ScheduledExecutorService serviceHost = context.getBean("RegisterExecutorService", ScheduledExecutorService.class);
            serviceHost.shutdownNow();
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();

        serviceFacade.setScheduleExecutorService(service);

        return serviceFacade;
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
