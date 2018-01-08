package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;

import java.util.Date;
import java.util.List;

@Repository("TaskMonitorInfoRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface TaskMonitorInfoRepository extends JpaRepository<TaskMonitorInfo, Long> {
    TaskMonitorInfo findOneByServiceIdentity(String serviceIdentity);
    @Query(value = "SELECT t FROM task_monitor_info t WHERE t.last_heartbeat_time NOT BETWEEN ?1 AND ?2",
            nativeQuery = true)
    List<TaskMonitorInfo> findAllByLastHeartbeatTime(Date beginDate, Date endDate);
}
