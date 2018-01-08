package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.LogCollect;

import java.util.Date;

@Repository("LogCollectRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface LogCollectRepository extends JpaRepository<LogCollect, Long> {
    Page<LogCollect> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
    @Query("SELECT e FROM log_collect e WHERE e.logTime BETWEEN :startDate AND :endDate")
    Page<LogCollect> findAllByDashboard(Date beginDate, Date endDate, Pageable pageable);
}
