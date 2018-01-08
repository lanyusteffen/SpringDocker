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

import java.util.List;

@Service("TaskMonitorInfoServiceReadonly")
public class TaskMonitorInfoService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("TaskMonitorInfoRepositoryReadonly")
    private TaskMonitorInfoRepository taskMonitorInfoRepository;

    public Page<TaskMonitorInfo> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "id");
        return taskMonitorInfoRepository.findAll(pageable);
    }

    public List<TaskMonitorInfo> getDashboard() {
        SearchDateStamp searchDate = getTodaySearchDate();
        return taskMonitorInfoRepository.findAllByLastHeartbeatTime(searchDate.getBeginDate(), searchDate.getEndDate());
    }

    public TaskMonitorInfo getDetail(long id) {
        return taskMonitorInfoRepository.getOne(id);
    }

    public TaskMonitorInfo getTaskMonitorInfoByServiceIdentity(String serviceIdentity) {
        return taskMonitorInfoRepository.findOneByServiceIdentity(serviceIdentity);
    }
}
