package stu.lanyu.springdocker.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
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
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.MonitorSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.utility.DateUtility;

import java.time.Duration;
import java.util.Date;
import java.util.List;
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
    RedisURI sentinelConfiguration () {

       RedisURI.Builder builder = RedisURI.builder().withSentinelMasterId(properties.getSentinel().getMaster());

        List<String> nodes = properties.getSentinel().getNodes();

        for (int i = 0; i < nodes.size(); i++) {

            String node  = nodes.get(i);
            String[] ipAndPort = node.split(":");

            builder.withSentinel(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
        }

        return builder.build();
    }

    @Bean
    StatefulRedisSentinelConnection<String, String> redisConnectionFactory() {

        RedisClient client = RedisClient.create(sentinelConfiguration());
        StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel();
        return connection;
    }

    @Bean
    @Qualifier("RedisSubscriberConnectionFactory")
    @Scope("singleton")
    RedisClient getRedisSubscriberConnectionFactory(){

        RedisURI.Builder builder = RedisURI.builder().withHost(redisMessageProperties.getHost())
                .withPort(redisMessageProperties.getPort())
                .withPassword(redisMessageProperties.getPassword())
                .withTimeout(Duration.ofMillis(redisMessageProperties.getTimeOut()));

        return RedisClient.create(builder.build());
    }

    @Bean("RedisSubscriberMessagePool")
    @Scope("singleton")
    RedisClient getRedisMessagePool() {

        RedisURI.Builder builder = RedisURI.builder().withHost(redisMessageProperties.getHost())
                .withPort(redisMessageProperties.getPort())
                .withPassword(redisMessageProperties.getPassword())
                .withTimeout(Duration.ofMillis(redisMessageProperties.getTimeOut()));

        return RedisClient.create(builder.build());
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

            RedisClient client = context.getBean("RedisSubscriberMessagePool",
                    RedisClient.class);
            RedisPubSubCommands<String, String> redis = client.connectPubSub().sync();

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                        "Start subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");

                redis.addListener(logCollectSubscriber);
                redis.subscribe(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);

                try {
                    redis.shutdown(true);
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
