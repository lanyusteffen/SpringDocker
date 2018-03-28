package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.entity.LogCollect;
import stu.lanyu.springdocker.repository.readonly.LogCollectRepository;
import stu.lanyu.springdocker.response.PagedResult;

import java.util.Date;
import java.util.List;

@Service("LogCollectServiceReadonly")
public class LogCollectService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("LogCollectRepositoryReadonly")
    private LogCollectRepository logCollectRepository;

    public Page<LogCollect> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "logTime");
        return logCollectRepository.findAll(pageable);
    }

    public LogCollect getDetail(long id) {
        return logCollectRepository.getOne(id);
    }

    public PagedResult<LogCollect> getDashboard(int totalResults) {
        AbstractBusinessService.SearchDateStamp searchDate = getTodaySearchDate(true);
        List<LogCollect> logCollectList = logCollectRepository
                .findAllByLogTimeBetween(Date.from(searchDate.getBeginDate().toInstant()),
                        Date.from(searchDate.getEndDate().toInstant()));
        return new PagedResult<>(logCollectList.size() > totalResults
                ? logCollectList.subList(0, totalResults) : logCollectList, logCollectList.size(), 0, totalResults);
    }

    public Page<LogCollect> getListPagedByServiceIdentity(String serviceIdentity, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "logTime");
        return logCollectRepository.findAllByServiceIdentity(serviceIdentity, pageable);
    }
}
