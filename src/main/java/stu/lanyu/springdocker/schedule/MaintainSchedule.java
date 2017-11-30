package stu.lanyu.springdocker.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.config.RedisMessageProperties;
import stu.lanyu.springdocker.message.ScheduledExecutorServiceFacade;
import stu.lanyu.springdocker.message.subscriber.HeartbeatSubscriber;
import stu.lanyu.springdocker.message.subscriber.LogCollectSubscriber;
import stu.lanyu.springdocker.message.subscriber.RegisterSubscriber;
import stu.lanyu.springdocker.message.subscriber.WarningSubscriber;
import stu.lanyu.springdocker.utility.DateUtility;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class MaintainSchedule {

    @Autowired(required = true)
    private ApplicationContext context;

    private JedisPool getJedisPool() {
        return context.getBean("RedisSubscriberMessagePool",
                JedisPool.class);
    }

    private ScheduledExecutorService getHeartbeatScheduledExecutorService(ScheduledExecutorServiceFacade serviceHeartbeatFacade) {
        HeartbeatSubscriber heartbeatSubscriber = context.getBean(HeartbeatSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskHeartbeatChannel!");
                jedis = pool.getResource();
                try {
                    serviceHeartbeatFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);
                }catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskHeartbeatChannel unsubscribe error: " + e.getMessage());
                }

                jedis.subscribe(heartbeatSubscriber, GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);

                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskHeartbeatChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskHeartbeatChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskHeartbeatChannel restore error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            serviceHeartbeatFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceHeartbeatFacade.setScheduleExecutorService(service);
        serviceHeartbeatFacade.setLastSubscribeTime(new Date());
        serviceHeartbeatFacade.setSubscriber(heartbeatSubscriber);

        return service;
    }

    private ScheduledExecutorService getWarningScheduledExecutorService(ScheduledExecutorServiceFacade serviceWarningFacade) {
        WarningSubscriber warningSubscriber = context.getBean(WarningSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");
                jedis = pool.getResource();
                try {
                    serviceWarningFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel unsubscribe error: " + e.getMessage());
                }

                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);

                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskWarningChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskWarningChannel restore error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            serviceWarningFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceWarningFacade.setScheduleExecutorService(service);
        serviceWarningFacade.setLastSubscribeTime(new Date());
        serviceWarningFacade.setSubscriber(warningSubscriber);

        return service;
    }

    private ScheduledExecutorService getRegisterScheduledExecutorService(ScheduledExecutorServiceFacade serviceRegisterFacade) {
        RegisterSubscriber registerSubscriber = context.getBean(RegisterSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskRegisterChannel!");
                jedis = pool.getResource();
                try {
                    serviceRegisterFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);
                }catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskRegisterChannel unsubscribe error: " + e.getMessage());
                }

                jedis.subscribe(registerSubscriber, GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);

                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskRegisterChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskRegisterChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskRegisterChannel restore error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }

            serviceRegisterFacade.getScheduleExecutorService().shutdownNow();
        });

        serviceRegisterFacade.setScheduleExecutorService(service);
        serviceRegisterFacade.setLastSubscribeTime(new Date());
        serviceRegisterFacade.setSubscriber(registerSubscriber);

        return service;
    }

    private ScheduledExecutorService getLogCollectScheduledExecutorService(ScheduledExecutorServiceFacade serviceLogCollectFacade) {
        LogCollectSubscriber logCollectSubscriber = context.getBean(LogCollectSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "Start restore subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");
                jedis = pool.getResource();
                try {
                    serviceLogCollectFacade.getSubscriber().unsubscribe(GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
                }catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel unsubscribe error: " + e.getMessage());
                }

                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);

                try {
                    jedis.quit();
                } catch (Exception e) {
                    System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel jedis quit error: " + e.getMessage());
                }
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End restore subscribe channel ESFTask.Commands.ESFTaskPushLogChannel!");
            } catch (Exception e) {
                System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "ESFTask.Commands.ESFTaskPushLogChannel restore error: " + e.getMessage());
            }
            finally {
                if (jedis != null)
                    jedis.close();
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

        ScheduledExecutorServiceFacade serviceHeartbeatFacade = context.getBean("HeartbeatExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceHeartbeatFacade != null) {

            if (serviceHeartbeatFacade.getScheduleExecutorService().isShutdown()) {

                getHeartbeatScheduledExecutorService(serviceHeartbeatFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceHeartbeatFacade.getLastSubscribeTime()) > 1.00) {

                    serviceHeartbeatFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        ScheduledExecutorServiceFacade serviceLogCollectFacade = context.getBean("LogCollectExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceLogCollectFacade != null) {

            if (serviceLogCollectFacade.getScheduleExecutorService().isShutdown()) {

                getLogCollectScheduledExecutorService(serviceLogCollectFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceHeartbeatFacade.getLastSubscribeTime()) > 1.00) {

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

                if (DateUtility.compareFormNowByHour(serviceHeartbeatFacade.getLastSubscribeTime()) > 1.00) {

                    serviceWarningFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        ScheduledExecutorServiceFacade serviceRegisterFacade = context.getBean("RegisterExecutorService",
                ScheduledExecutorServiceFacade.class);

        if (serviceRegisterFacade != null) {

            if (serviceRegisterFacade.getScheduleExecutorService().isShutdown()) {

                getRegisterScheduledExecutorService(serviceRegisterFacade);
            }
            else {

                if (DateUtility.compareFormNowByHour(serviceHeartbeatFacade.getLastSubscribeTime()) > 1.00) {

                    serviceRegisterFacade.getScheduleExecutorService().shutdownNow();
                }
            }
        }

        System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" + "End MaintainSchedule!");
    }
}
