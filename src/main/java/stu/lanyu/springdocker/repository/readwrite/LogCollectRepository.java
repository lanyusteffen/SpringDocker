package stu.lanyu.springdocker.repository.readwrite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readwrite;
import stu.lanyu.springdocker.domain.entity.LogCollect;

@Repository("LogCollectRepositoryReadwrite")
@Readwrite
@Transactional
public interface LogCollectRepository extends JpaRepository<LogCollect, Long> {
}
