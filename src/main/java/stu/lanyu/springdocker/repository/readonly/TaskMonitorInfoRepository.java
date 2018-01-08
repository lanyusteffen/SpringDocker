package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.TaskMonitorInfo;

import java.util.Date;

@Repository("TaskMonitorInfoRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface TaskMonitorInfoRepository extends JpaRepository<TaskMonitorInfo, Long> {
    TaskMonitorInfo findOneByServiceIdentity(String serviceIdentity);
    @Query("SELECT e FROM task_monitor_info e WHERE e.lastHeartbeatTime NOT BETWEEN :startDate AND :endDate")
    Page<TaskMonitorInfo> findAllByDashboard(Date beginDate, Date endDate, Pageable pageable);
}
