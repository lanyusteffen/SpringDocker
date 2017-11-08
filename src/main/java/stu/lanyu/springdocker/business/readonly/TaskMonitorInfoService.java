package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.repository.readonly.TaskMonitorInfoRepository;

@Service("TaskMonitorInfoServiceReadonly")
public class TaskMonitorInfoService {

    @Autowired(required = true)
    @Qualifier("TaskMonitorInfoRepositoryReadonly")
    private TaskMonitorInfoRepository taskMonitorInfoRepository;

    public Page<TaskMonitorInfo> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return taskMonitorInfoRepository.findAll(pageable);
    }

    public TaskMonitorInfo getListPagedByServiceIdentity(String serviceIdentity) {
        return taskMonitorInfoRepository.findOneByServiceIdentity(serviceIdentity);
    }
}
