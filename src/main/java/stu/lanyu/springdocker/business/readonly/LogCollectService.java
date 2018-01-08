package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.LogCollect;
import stu.lanyu.springdocker.repository.readonly.LogCollectRepository;

@Service("LogCollectServiceReadonly")
public class LogCollectService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("LogCollectRepositoryReadonly")
    private LogCollectRepository logCollectRepository;

    public Page<LogCollect> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "logTime");
        return logCollectRepository.findAll(pageable);
    }

    public LogCollect getDetail(long id) {
        return logCollectRepository.getOne(id);
    }

    public Page<LogCollect> getDashboard() {
        Pageable pageable = new PageRequest(1, Integer.MAX_VALUE, Sort.Direction.DESC, "Id");
        AbstractBusinessService.TodaySearchDate searchDate = getTodaySearchDate();
        return logCollectRepository.findAllByDashboard(searchDate.getBeginDate(), searchDate.getEndDate(), pageable);
    }

    public Page<LogCollect> getListPagedByServiceIdentity(String serviceIdentity, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.DESC, "logTime");
        return logCollectRepository.findAllByServiceIdentity(serviceIdentity, pageable);
    }
}
