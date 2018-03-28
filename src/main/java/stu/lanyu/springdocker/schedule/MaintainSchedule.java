package stu.lanyu.springdocker.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MaintainSchedule {

    @Autowired(required = true)
    private ApplicationContext context;
}
