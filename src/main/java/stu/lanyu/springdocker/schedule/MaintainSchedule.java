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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class MaintainSchedule {

    @Autowired(required = true)
    private ApplicationContext context;

    @Autowired(required = true)
    private RedisMessageProperties redisMessageProperties;

    private JedisPool getJedisPool() {
        return context.getBean("RedisSubscriberMessagePool",
                JedisPool.class);
    }

    private ScheduledExecutorService getHeartbeatScheduledExecutorService() {
        HeartbeatSubscriber heartbeatSubscriber = context.getBean(HeartbeatSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(heartbeatSubscriber, GlobalConfig.Redis.ESFTASK_HEARTBEAT_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getWarningScheduledExecutorService() {
        WarningSubscriber warningSubscriber = context.getBean(WarningSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(warningSubscriber, GlobalConfig.Redis.ESFTASK_WARNING_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getRegisterScheduledExecutorService() {
        RegisterSubscriber registerSubscriber = context.getBean(RegisterSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(registerSubscriber, GlobalConfig.Redis.ESFTASK_REGISTER_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    private ScheduledExecutorService getLogCollectScheduledExecutorService() {
        LogCollectSubscriber logCollectSubscriber = context.getBean(LogCollectSubscriber.class);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.execute(() -> {

            JedisPool pool = getJedisPool();
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                jedis.subscribe(logCollectSubscriber, GlobalConfig.Redis.ESFTASK_PUSHLOG_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (jedis != null)
                    jedis.close();
            }
        });
        return service;
    }

    @Scheduled(fixedDelay = 50000, initialDelay = 50000)
    public void checkSubscriber() {

        ScheduledExecutorServiceFacade serviceHeartbeatFacade = context.getBean("HeartbeatExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceHeartbeatFacade != null) {

            if (serviceHeartbeatFacade.getScheduleExecutorService().isShutdown()) {

                serviceHeartbeatFacade.setScheduleExecutorService(getHeartbeatScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceLogCollectFacade = context.getBean("LogCollectExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceLogCollectFacade != null) {

            if (serviceLogCollectFacade.getScheduleExecutorService().isShutdown()) {

                serviceLogCollectFacade.setScheduleExecutorService(getLogCollectScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceWarningFacade = context.getBean("WarningExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceWarningFacade != null) {

            if (serviceWarningFacade.getScheduleExecutorService().isShutdown()) {

                serviceWarningFacade.setScheduleExecutorService(getWarningScheduledExecutorService());
            }
        }

        ScheduledExecutorServiceFacade serviceRegisterFacade = context.getBean("RegisterExecutorService", ScheduledExecutorServiceFacade.class);

        if (serviceRegisterFacade != null) {

            if (serviceRegisterFacade.getScheduleExecutorService().isShutdown()) {

                serviceRegisterFacade.setScheduleExecutorService(getRegisterScheduledExecutorService());
            }
        }
    }
}
