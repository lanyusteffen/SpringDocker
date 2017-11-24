package stu.lanyu.springdocker.message;

import java.util.concurrent.ScheduledExecutorService;

public class ScheduledExecutorServiceFacade {

    private ScheduledExecutorService scheduleExecutorService;

    public ScheduledExecutorService getScheduleExecutorService() {
        return scheduleExecutorService;
    }

    public void setScheduleExecutorService(ScheduledExecutorService scheduleExecutorService) {
        this.scheduleExecutorService = scheduleExecutorService;
    }
}
