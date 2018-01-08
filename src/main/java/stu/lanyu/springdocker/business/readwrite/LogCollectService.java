package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.repository.readwrite.LogCollectRepository;

import java.util.ArrayList;
import java.util.List;

@Service("LogCollectServiceReadwrite")
public class LogCollectService extends AbstractBusinessService {
    @Autowired(required = true)
    @Qualifier("LogCollectRepositoryReadwrite")
    private LogCollectRepository logCollectRepository;

    public void save(LogCollect logCollect) {
        logCollectRepository.save(logCollect);
    }

    public void saveInBatch(ArrayList<LogCollect> logCollectIterable) {
        logCollectRepository.save(logCollectIterable);
    }
}
