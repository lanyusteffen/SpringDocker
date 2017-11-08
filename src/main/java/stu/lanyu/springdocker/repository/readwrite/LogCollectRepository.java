package stu.lanyu.springdocker.repository.readwrite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadWrite;
import stu.lanyu.springdocker.domain.LogCollect;

import java.util.List;

@Repository("LogCollectRepositoryReadwrite")
@ReadWrite
@Transactional
public interface LogCollectRepository extends JpaRepository<LogCollect, Long> {
}
