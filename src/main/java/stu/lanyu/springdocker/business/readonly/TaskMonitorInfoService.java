package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;
import stu.lanyu.springdocker.repository.readonly.TaskMonitorInfoRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;
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

    private SearchDateStamp getHeartbeatDashboardShowRule() {

        Date endDate = Date.from(Instant.now().minusMillis(GlobalConfig.WebConfig.BAD_HEARTBEAT_DASHBOARD_SHOWRULE));

        LocalDate localBeginDate = LocalDate.now().minusYears(1);
        Instant instantForBegin = localBeginDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date beginDate = Date.from(instantForBegin);

        return new SearchDateStamp(beginDate, endDate);
    }

    public List<TaskMonitorInfo> getDashboard() {
        SearchDateStamp searchDate = getHeartbeatDashboardShowRule();
        return taskMonitorInfoRepository.findAllByLastHeartbeatTimeBetween(searchDate.getBeginDate(), searchDate.getEndDate());
    }

    public TaskMonitorInfo getDetail(long id) {
        return taskMonitorInfoRepository.getOne(id);
    }

    public TaskMonitorInfo getTaskMonitorInfoByServiceIdentity(String serviceIdentity) {
        return taskMonitorInfoRepository.findOneByServiceIdentity(serviceIdentity);
    }
}
