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
import java.util.List;

@Repository("LogCollectRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface LogCollectRepository extends JpaRepository<LogCollect, Long> {
    Page<LogCollect> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
    @Query(value = "SELECT l FROM log_collect l WHERE l.logTime BETWEEN ?1 AND ?2",
            countQuery = "SELECT COUNT(1) FROM log_collect WHERE log_time BETWEEN ?1 AND ?2",
            nativeQuery = true)
    List<LogCollect> findAllByLogTime(Date beginDate, Date endDate);
}
