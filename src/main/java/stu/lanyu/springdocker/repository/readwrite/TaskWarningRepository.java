package stu.lanyu.springdocker.repository.readwrite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readwrite;
import stu.lanyu.springdocker.domain.entity.TaskWarning;

@Repository("TaskWarningRepositoryReadwrite")
@Readwrite
@Transactional
public interface TaskWarningRepository extends JpaRepository<TaskWarning, Long> {
}
