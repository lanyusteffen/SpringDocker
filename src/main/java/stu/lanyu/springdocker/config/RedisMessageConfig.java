package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.MonitorSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableConfigurationProperties(RedisMessageProperties.class)
public class RedisMessageConfig {

    @Autowired(required = true)
    private RedisMessageProperties redisMessageProperties;

    @Autowired(required = true)
    private ApplicationContext context;

    @Autowired(required = true)
    private RedisProperties properties;

    // Construct the RedisSentinelConfiguration using all the nodes in RedisSentinelNodes
    RedisSentinelConfiguration sentinelConfiguration () {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(properties.getSentinel().getMaster());

        String[] nodes = properties.getSentinel().getNodes().split(",");

        for (int i = 0; i < nodes.length; i++) {
            String node  = nodes[i];
            String[] ipAndPort = node.split(":");

            sentinelConfig.sentinel(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
        }

        return sentinelConfig;
    }

    @Bean
    JedisConnectionFactory redisConnectionFactory() {

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration());
        jedisConnectionFactory.setUsePool(true);

        jedisConnectionFactory.setPassword(properties.getPassword());

        JedisPoolConfig poolConfig = jedisConnectionFactory.getPoolConfig();
        poolConfig.setMaxIdle(properties.getPool().getMaxIdle());
        poolConfig.setMinIdle(properties.getPool().getMinIdle());
        poolConfig.setMaxTotal(properties.getPool().getMaxActive());
        // poolConfig.setMaxTotal(properties.getPool().getMaxTotal);
        poolConfig.setMaxWaitMillis(properties.getPool().getMaxWait());

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        jedisConnectionFactory.setPoolConfig(poolConfig);

        jedisConnectionFactory.afterPropertiesSet();

        return jedisConnectionFactory;
    }

    @Bean
    @Qualifier("RedisSubscriberConnectionFactory")
    @Scope("singleton")
    JedisConnectionFactory getRedisSubscriberConnectionFactory(){

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

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

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
    LogCollectSubscriber getLogCollectSubscriber() {
        return new LogCollectSubscriber();
    }

    @Bean
    MonitorSubscriber getRegisterSubscriber() {
        return new MonitorSubscriber();
    }

    @Bean
    WarningSubscriber getWarningSubscriber() {
        return new WarningSubscriber();
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
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "Start subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");
                jedis = pool.getResource();
                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                            "ESFTask.Commands.ESFTaskPushLogChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "End subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "ESFTask.Commands.ESFTaskPushLogChannel subscribe error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            ScheduledExecutorServiceFacade serviceFacade = context.getBean("LogCollectExecutorService",
                    ScheduledExecutorServiceFacade.class);
            serviceFacade.getScheduleExecutorService().shutdownNow();
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();
        serviceFacade.setScheduleExecutorService(service);
        serviceFacade.setLastSubscribeTime(new Date());
        serviceFacade.setSubscriber(logCollectSubscriber);

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
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "Start subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");
                jedis = pool.getResource();
                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                            "ESFTask.Commands.ESFTaskWarningChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "End subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "ESFTask.Commands.ESFTaskWarningChannel subscribe error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            ScheduledExecutorServiceFacade serviceFacade = context.getBean("WarningExecutorService",
                    ScheduledExecutorServiceFacade.class);
            serviceFacade.getScheduleExecutorService().shutdownNow();
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();
        serviceFacade.setScheduleExecutorService(service);
        serviceFacade.setLastSubscribeTime(new Date());
        serviceFacade.setSubscriber(warningSubscriber);

        return serviceFacade;
    }

    @Bean(name = "MonitorExecutorService")
    @Scope("singleton")
    ScheduledExecutorServiceFacade getMonitorScheduleExecutorService(MonitorSubscriber monitorSubscriber) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = context.getBean("RedisSubscriberMessagePool",
                    JedisPool.class);
            Jedis jedis = null;

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start subscribe channel ESFTask.Commands.ESFTaskMonitorChannel!");
                jedis = pool.getResource();
                jedis.subscribe(monitorSubscriber, GlobalConfig.Redis.ESFTASK_MONITOR_CHANNEL);
                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskMonitorChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End subscribe channel ESFTask.Commands.ESFTaskMonitorChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskMonitorChannel subscribe error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            ScheduledExecutorServiceFacade serviceFacade = context.getBean("MonitorExecutorService",
                    ScheduledExecutorServiceFacade.class);
            serviceFacade.getScheduleExecutorService().shutdownNow();
        });

        ScheduledExecutorServiceFacade serviceFacade = new ScheduledExecutorServiceFacade();
        serviceFacade.setScheduleExecutorService(service);
        serviceFacade.setLastSubscribeTime(new Date());
        serviceFacade.setSubscriber(monitorSubscriber);

        return serviceFacade;
    }
}
