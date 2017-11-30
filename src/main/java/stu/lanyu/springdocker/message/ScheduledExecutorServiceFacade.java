package stu.lanyu.springdocker.message;

import redis.clients.jedis.JedisPubSub;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledExecutorServiceFacade {

    private ScheduledExecutorService scheduleExecutorService;

    public ScheduledExecutorService getScheduleExecutorService() {
        return scheduleExecutorService;
    }

    public void setScheduleExecutorService(ScheduledExecutorService scheduleExecutorService) {
        this.scheduleExecutorService = scheduleExecutorService;
    }

    private Date lastSubscribeTime;

    public Date getLastSubscribeTime() {
        return lastSubscribeTime;
    }

    public void setLastSubscribeTime(Date lastSubscribeTime) {
        this.lastSubscribeTime = lastSubscribeTime;
    }

    private JedisPubSub subscriber;

    public JedisPubSub getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(JedisPubSub subscriber) {
        this.subscriber = subscriber;
    }
}
