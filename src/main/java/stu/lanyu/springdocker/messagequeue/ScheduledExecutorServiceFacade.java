package stu.lanyu.springdocker.messagequeue;

import io.lettuce.core.pubsub.RedisPubSubListener;

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

    private RedisPubSubListener<String, String> subscriber;

    public RedisPubSubListener<String, String> getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(RedisPubSubListener<String, String> subscriber) {
        this.subscriber = subscriber;
    }
}
