package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.entity.LogCollect;

import java.util.Date;
import java.util.List;

@Repository("LogCollectRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface LogCollectRepository extends JpaRepository<LogCollect, Long> {
    Page<LogCollect> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
    List<LogCollect> findAllByLogTimeBetween(Date beginDate, Date endDate);
}
