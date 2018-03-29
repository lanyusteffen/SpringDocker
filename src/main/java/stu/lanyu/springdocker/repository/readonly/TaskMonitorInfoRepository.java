package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readonly;
import stu.lanyu.springdocker.domain.entity.TaskMonitorInfo;

import java.util.Date;
import java.util.List;

@Repository("TaskMonitorInfoRepositoryReadonly")
@Readonly
@Transactional(readOnly = true)
public interface TaskMonitorInfoRepository extends JpaRepository<TaskMonitorInfo, Long> {
    TaskMonitorInfo findOneByServiceIdentity(String serviceIdentity);
    List<TaskMonitorInfo> findAllByLastHeartbeatTimeBetween(Date beginDate, Date endDate);
    List<TaskMonitorInfo> findAllByServiceIdentityIn(List<String> serviceIdentityList);
}
