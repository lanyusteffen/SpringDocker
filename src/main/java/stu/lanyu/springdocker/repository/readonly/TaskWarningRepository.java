package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readonly;
import stu.lanyu.springdocker.domain.entity.TaskWarning;

import java.util.Date;
import java.util.List;

@Repository("TaskWarningRepositoryReadonly")
@Readonly
@Transactional(readOnly = true)
public interface TaskWarningRepository extends JpaRepository<TaskWarning, Long> {
    Page<TaskWarning> findAllByJobNameAndJobGroup(String jobName, String jobGroup, Pageable pageable);
    Page<TaskWarning> findOneByServiceIdentity(String serviceIdentity, Pageable pageable);
    List<TaskWarning> findAllByAddTimeBetween(Date beginDate, Date endDate);
}
