package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.repository.readonly.TaskWarningRepository;

import java.util.List;

@Service("TaskWarningServiceReadonly")
public class TaskWarningService {

    @Autowired(required = true)
    @Qualifier("TaskWarningRepositoryReadonly")
    private TaskWarningRepository taskWarningRepository;

    public Page<TaskWarning> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return taskWarningRepository.findAll(pageable);
    }

    public Page<TaskWarning> getListPagedByServiceIdentity(String serviceIdentity, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return taskWarningRepository.findOneByServiceIdentity(serviceIdentity, pageable);
    }

    public Page<TaskWarning> getListPagedByJob(String jobName, String jobGroup, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "Id");
        return taskWarningRepository.findAllByJobNameAndJobGroup(jobName, jobGroup, pageable);
    }
}
