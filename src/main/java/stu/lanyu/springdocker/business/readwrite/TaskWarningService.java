package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.repository.readwrite.TaskWarningRepository;

@Service("TaskWarningServiceReadwrite")
public class TaskWarningService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("TaskWarningRepositoryReadwrite")
    private TaskWarningRepository taskWarningRepository;

    public void save(TaskWarning taskWarning) {
        taskWarningRepository.save(taskWarning);
    }
}
