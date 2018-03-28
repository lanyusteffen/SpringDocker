package stu.lanyu.springdocker.repository.readwrite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadWrite;
import stu.lanyu.springdocker.domain.entity.TaskMonitorInfo;

@Repository("TaskMonitorInfoRepositoryReadwrite")
@ReadWrite
@Transactional
public interface TaskMonitorInfoRepository extends JpaRepository<TaskMonitorInfo, Long> {
}
