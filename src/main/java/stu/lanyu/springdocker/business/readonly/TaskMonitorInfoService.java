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
import stu.lanyu.springdocker.response.PagedResult;

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

        LocalDateTime dtEnd = LocalDateTime.now().minusSeconds(GlobalConfig.WebConfig.BAD_HEARTBEAT_DASHBOARD_SHOWRULE);

        LocalDateTime dtStart = LocalDate.now().atStartOfDay();
        ZonedDateTime zdt = dtStart.atZone(ZoneId.systemDefault());
        Instant instant = LocalDate.now().atStartOfDay().toInstant(zdt.getOffset());
        ZonedDateTime beginDate = ZonedDateTime.ofInstant(instant , zoneId);

        ZonedDateTime zdtEnd = dtEnd.atZone(ZoneId.systemDefault());
        instant = dtEnd.toInstant(zdtEnd.getOffset());
        ZonedDateTime endDate = ZonedDateTime.ofInstant(instant , zoneId);

        return new SearchDateStamp(beginDate, endDate);
    }

    public PagedResult<TaskMonitorInfo> getDashboard(int totalResults) {
        SearchDateStamp searchDate = getHeartbeatDashboardShowRule(true);
        List<TaskMonitorInfo> taskMonitorInfoList = taskMonitorInfoRepository
                .findAllByLastHeartbeatTimeBetween(Date.from(searchDate.getBeginDate().toInstant()),
                        Date.from(searchDate.getEndDate().toInstant()));
        return new PagedResult<>(taskMonitorInfoList.size() > totalResults
                ? taskMonitorInfoList.subList(0, totalResults) : taskMonitorInfoList, totalResults, 0, totalResults);
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
