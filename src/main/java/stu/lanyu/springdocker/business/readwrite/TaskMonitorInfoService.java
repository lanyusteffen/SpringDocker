package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.repository.readwrite.TaskMonitorInfoRepository;

import java.util.List;

@Service("TaskMonitorInfoServiceReadwrite")
public class TaskMonitorInfoService {
    @Autowired(required = true)
    @Qualifier("TaskMonitorInfoRepositoryReadwrite")
    private TaskMonitorInfoRepository taskMonitorInfoRepository;

    public void save(TaskMonitorInfo taskMonitorInfo) {
        taskMonitorInfoRepository.save(taskMonitorInfo);
    }

    public void saveBatch(List<TaskMonitorInfo> taskMonitorInfoList) {
        taskMonitorInfoRepository.saveAll(taskMonitorInfoList);
    }
}
