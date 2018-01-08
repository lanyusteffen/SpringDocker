package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.repository.readonly.TaskMonitorInfoRepository;

import java.util.Calendar;
import java.util.Date;

@Service("TaskMonitorInfoServiceReadonly")
public class TaskMonitorInfoService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("TaskMonitorInfoRepositoryReadonly")
    private TaskMonitorInfoRepository taskMonitorInfoRepository;

    public Page<TaskMonitorInfo> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "id");
        return taskMonitorInfoRepository.findAll(pageable);
    }

    public Page<TaskMonitorInfo> getDashboard() {
        Pageable pageable = new PageRequest(1, Integer.MAX_VALUE, Sort.Direction.DESC, "Id");
        TodaySearchDate searchDate = getTodaySearchDate();
        return taskMonitorInfoRepository.findAllByDashboard(searchDate.getBeginDate(), searchDate.getEndDate(), pageable);
    }

    public TaskMonitorInfo getDetail(long id) {
        return taskMonitorInfoRepository.getOne(id);
    }

    public TaskMonitorInfo getTaskMonitorInfoByServiceIdentity(String serviceIdentity) {
        return taskMonitorInfoRepository.findOneByServiceIdentity(serviceIdentity);
    }
}
