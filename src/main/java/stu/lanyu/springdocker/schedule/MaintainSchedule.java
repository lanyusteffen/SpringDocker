package stu.lanyu.springdocker.schedule;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.MonitorSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class MaintainSchedule {

    @Autowired(required = true)
    private ApplicationContext context;

    private RedisClient getJedisPool() {
        return context.getBean("RedisSubscriberMessagePool",
                RedisClient.class);
    }

    private ScheduledExecutorService getWarningScheduledExecutorService(ScheduledExecutorServiceFacade serviceWarningFacade) {
        WarningSubscriber warningSubscriber = context.getBean(WarningSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            RedisClient client = getJedisPool();
            StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");

                connection.addListener(warningSubscriber);
                RedisPubSubCommands<String, String> redis = connection.sync();
                redis.subscribe(GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);

                try {
                    redis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel restore error: " + e.getMessage());
            }
            finally {
                if (connection != null)
                    connection.close();
            }

            serviceWarningFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceWarningFacade.setScheduleExecutorService(service);
        serviceWarningFacade.setLastSubscribeTime(new Date());
        serviceWarningFacade.setSubscriber(warningSubscriber);

        return service;
    }

    private ScheduledExecutorService getMonitorScheduledExecutorService(ScheduledExecutorServiceFacade serviceRegisterFacade) {
        MonitorSubscriber monitorSubscriber = context.getBean(MonitorSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            RedisClient client = getJedisPool();
            StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskMonitorChannel!");

                connection.addListener(monitorSubscriber);
                RedisPubSubCommands<String, String> redis = connection.sync();
                redis.subscribe(GlobalConfig.Redis.ESFTASK_MONITOR_CHANNEL);

                try {
                    redis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskMonitorChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskMonitorChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskMonitorChannel restore error: " + e.getMessage());
            }
            finally {
                if (connection != null)
                    connection.close();
            }

            serviceRegisterFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceRegisterFacade.setScheduleExecutorService(service);
        serviceRegisterFacade.setLastSubscribeTime(new Date());
        serviceRegisterFacade.setSubscriber(monitorSubscriber);

        return service;
    }

    private ScheduledExecutorService getLogCollectScheduledExecutorService(ScheduledExecutorServiceFacade serviceLogCollectFacade) {
        LogCollectSubscriber logCollectSubscriber = context.getBean(LogCollectSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            RedisClient client = getJedisPool();
            StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");

                connection.addListener(logCollectSubscriber);
                RedisPubSubCommands<String, String> redis = connection.sync();
                redis.subscribe(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);

                try {
                    redis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel restore error: " + e.getMessage());
            }
            finally {
                if (connection != null)
                    connection.close();
            }

            serviceLogCollectFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceLogCollectFacade.setScheduleExecutorService(service);
        serviceLogCollectFacade.setLastSubscribeTime(new Date());
        serviceLogCollectFacade.setSubscriber(logCollectSubscriber);

        return service;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void checkSubscriber() {

        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start MaintainSchedule!");

        ScheduledExecutorServiceFacade serviceLogCollectFacade = context.getBean("LogCollectExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceLogCollectFacade != null) {

            if (serviceLogCollectFacade.getScheduleExecutorService().isShutdown()) {

                getLogCollectScheduledExecutorService(serviceLogCollectFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceLogCollectFacade.getLastSubscribeTime()) > GlobalConfig.WebConfig.REDIS_SUBSCRIBER_EXPIRE_HOUR) {

                    try {
                        serviceLogCollectFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
                    }catch (Exception e) {
                        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel unsubscribe error: " + e.getMessage());
                    }

                    serviceLogCollectFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        ScheduledExecutorServiceFacade serviceWarningFacade = context.getBean("WarningExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceWarningFacade != null) {

            if (serviceWarningFacade.getScheduleExecutorService().isShutdown()) {

                getWarningScheduledExecutorService(serviceWarningFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceWarningFacade.getLastSubscribeTime()) > GlobalConfig.WebConfig.REDIS_SUBSCRIBER_EXPIRE_HOUR) {

                    try {
                        serviceWarningFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
                    } catch (Exception e) {
                        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel unsubscribe error: " + e.getMessage());
                    }

                    serviceWarningFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        ScheduledExecutorServiceFacade serviceMonitorFacade = context.getBean("MonitorExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceMonitorFacade != null) {

            if (serviceMonitorFacade.getScheduleExecutorService().isShutdown()) {

                getMonitorScheduledExecutorService(serviceMonitorFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceMonitorFacade.getLastSubscribeTime()) > GlobalConfig.WebConfig.REDIS_SUBSCRIBER_EXPIRE_HOUR) {

                    try {
                        serviceMonitorFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_MONITOR_CHANNEL);
                    }catch (Exception e) {
                        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                                "ESFTask.Commands.ESFTaskMonitorChannel unsubscribe error: " + e.getMessage());
                    }

                    serviceMonitorFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End MaintainSchedule!");
    }
}
