package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.business.readwrite.LogCollectService;
import stu.lanyu.springdocker.domain.LogCollect;

import java.util.List;

@RestController
@RequestMapping(value = "/log")
public class LogController {

    @Qualifier(value = "LogCollectServiceReadonly")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readonly.LogCollectService logCollectQueryService;

    @Approve
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public @ResponseBody Page<LogCollect> getAll(int pageIndex, int pageSize) {
        Page<LogCollect> logCollectPage = logCollectQueryService.getListPaged(pageIndex, pageSize);
        return logCollectPage;
    }

    @Approve
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    public @ResponseBody LogCollect getDetail(long id) {
        LogCollect logCollect = logCollectQueryService.getDetail(id);
        return  logCollect;
    }

    @Approve
    @RequestMapping(value = "/getAllEachService", method = RequestMethod.GET)
    public @ResponseBody Page<LogCollect> getAllEachService(int pageIndex, int pageSize, String serviceIdentity) {
        Page<LogCollect> logCollectPage = logCollectQueryService.getListPagedByServiceIdentity(serviceIdentity, pageIndex, pageSize);
        return logCollectPage;
    }
}
