package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.repository.readonly.TaskWarningRepository;

import java.util.List;

@Service("TaskWarningServiceReadonly")
public class TaskWarningService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("TaskWarningRepositoryReadonly")
    private TaskWarningRepository taskWarningRepository;

    public Page<TaskWarning> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "id");
        return taskWarningRepository.findAll(pageable);
    }

    public TaskWarning getDetail(long id) {
        return taskWarningRepository.getOne(id);
    }

    public Page<TaskWarning> getListPagedByServiceIdentity(String serviceIdentity, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "id");
        return taskWarningRepository.findOneByServiceIdentity(serviceIdentity, pageable);
    }

    public Page<TaskWarning> getListPagedByJob(String jobName, String jobGroup, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "Id");
        return taskWarningRepository.findAllByJobNameAndJobGroup(jobName, jobGroup, pageable);
    }

    public List<TaskWarning> getDashboard() {
        AbstractBusinessService.SearchDateStamp searchDate = getTodaySearchDate();
        return taskWarningRepository.findAllByAddTime(searchDate.getBeginDate(), searchDate.getEndDate());
    }
}
