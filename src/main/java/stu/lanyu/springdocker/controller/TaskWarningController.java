package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.business.readwrite.TaskWarningService;
import stu.lanyu.springdocker.domain.TaskWarning;

import java.util.List;

@RestController
@RequestMapping(value = "/taskwarning")
public class TaskWarningController {

    @Qualifier(value = "TaskWarningServiceReadonly")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readonly.TaskWarningService taskWarningQueryService;

    @Approve
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public @ResponseBody List<TaskWarning> getAll(int pageIndex, int pageSize) {
        Page<TaskWarning> taskWarningPage = taskWarningQueryService.getListPaged(pageIndex, pageSize);
        return taskWarningPage.getContent();
    }

    @Approve
    @RequestMapping(value = "/getAllEachService", method = RequestMethod.GET)
    public @ResponseBody List<TaskWarning> getAllEachService(int pageIndex, int pageSize, String serviceIdentity) {
        Page<TaskWarning> taskWarningPage = taskWarningQueryService.getListPagedByServiceIdentity(serviceIdentity, pageIndex, pageSize);
        return taskWarningPage.getContent();
    }

    @Approve
    @RequestMapping(value = "/getAllByJob", method = RequestMethod.GET)
    public @ResponseBody List<TaskWarning> getAllByJob(int pageIndex, int pageSize, String jobName, String jobGroup) {
        Page<TaskWarning> taskWarningPage = taskWarningQueryService.getListPagedByJob(jobName, jobGroup, pageIndex, pageSize);
        return taskWarningPage.getContent();
    }
}
