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

import java.time.*;
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

    private SearchDateStamp getHeartbeatDashboardShowRule(boolean useUTC) {

        ZoneId zoneId = (useUTC ? ZoneId.of("UTC") : ZoneId.systemDefault());

        Instant instant = Instant.now();
        LocalDateTime dt = LocalDateTime.now().minusSeconds(GlobalConfig.WebConfig.BAD_HEARTBEAT_DASHBOARD_SHOWRULE);

        ZonedDateTime endDate = ZonedDateTime.ofInstant(instant , zoneId);

        ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
        instant = dt.toInstant(zdt.getOffset());
        ZonedDateTime beginDate = ZonedDateTime.ofInstant(instant , zoneId);

        return new SearchDateStamp(beginDate, endDate);
    }

    public List<TaskMonitorInfo> getDashboard() {
        SearchDateStamp searchDate = getHeartbeatDashboardShowRule(true);
        return taskMonitorInfoRepository.findAllByLastHeartbeatTimeBetween(Date.from(searchDate.getBeginDate().toInstant()), Date.from(searchDate.getEndDate().toInstant()));
    }

    public TaskMonitorInfo getDetail(long id) {
        return taskMonitorInfoRepository.getOne(id);
    }

    public TaskMonitorInfo getTaskMonitorInfoByServiceIdentity(String serviceIdentity) {
        return taskMonitorInfoRepository.findOneByServiceIdentity(serviceIdentity);
    }

    public List<TaskMonitorInfo> getTaskMonitorInfoByServiceIdentityInRange(List<String> serviceIdentityList) {
        return taskMonitorInfoRepository.findAllByServiceIdentityIn(serviceIdentityList);
    }
}
