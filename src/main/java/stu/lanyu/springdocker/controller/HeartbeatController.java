package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.business.readwrite.TaskMonitorInfoService;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;

import java.util.List;

@RestController
@RequestMapping(value = "/heartbeat")
public class HeartbeatController {

    @Qualifier(value = "TaskMonitorInfoServiceReadwrite")
    @Autowired(required = true)
    private TaskMonitorInfoService taskMonitorInfoService;
    @Qualifier(value = "TaskMonitorInfoServiceReadonly")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readonly.TaskMonitorInfoService taskMonitorInfoQueryService;

    @Approve
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public @ResponseBody Page<TaskMonitorInfo> getAll(int pageIndex, int pageSize) {
        Page<TaskMonitorInfo> taskMonitorInfoPage = taskMonitorInfoQueryService.getListPaged(pageIndex, pageSize);
        return taskMonitorInfoPage;
    }

    @Approve
    @RequestMapping(value = "/getDashBoard", method = RequestMethod.GET)
    public @ResponseBody List<TaskMonitorInfo> getDashBoard() {
        List<TaskMonitorInfo> taskMonitorInfoPage = taskMonitorInfoQueryService.getDashboard();
        return taskMonitorInfoPage;
    }

    @Approve
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    public @ResponseBody TaskMonitorInfo getDetail(long id) {
        TaskMonitorInfo taskMonitorInfo = taskMonitorInfoQueryService.getDetail(id);
        return taskMonitorInfo;
    }
}
