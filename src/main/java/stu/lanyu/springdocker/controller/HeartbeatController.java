package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.redis.entity.RegisterTask;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/heartbeat")
public class HeartbeatController {

    @Qualifier(value = "TaskMonitorInfoServiceReadwrite")
    @Autowired(required = true)
    private TaskMonitorInfoService taskMonitorInfoService;
    @Qualifier(value = "TaskMonitorInfoServiceReadonly")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService taskMonitorInfoQueryService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisTemplate<String, RegisterTask> redisTaskTemplate;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public @ResponseBody
    List<TaskMonitorInfo> getAll(int pageIndex, int pageSize) {
        Page<TaskMonitorInfo> taskMonitorInfoPage = taskMonitorInfoQueryService.getListPaged(pageIndex, pageSize);
        return taskMonitorInfoPage.getContent();
    }

    @RequestMapping(value = "/getAllHeartbeatUrl", method = RequestMethod.GET)
    public @ResponseBody List<String> getAllHeartbeatUrl() {
        return redisTemplate.opsForHash().values(GlobalConfig.Redis.REGISTER_HEARTBEAT_CACHE_KEY).stream()
                .map(r -> (String)r)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/getAllRegisterService", method = RequestMethod.GET)
    public @ResponseBody List<RegisterTask> getAllRegisterService() {
        return redisTaskTemplate.opsForHash().values(GlobalConfig.Redis.REGISTER_TASK_CACHE_KEY).stream()
                .map(r -> (RegisterTask)r)
                .collect(Collectors.toList());
    }
}
