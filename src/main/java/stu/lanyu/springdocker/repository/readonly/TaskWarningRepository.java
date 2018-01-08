package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.TaskWarning;

import java.util.Date;
import java.util.List;

@Repository("TaskWarningRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface TaskWarningRepository extends JpaRepository<TaskWarning, Long> {
    Page<TaskWarning> findAllByJobNameAndJobGroup(String jobName, String jobGroup, Pageable pageable);
    Page<TaskWarning> findOneByServiceIdentity(String serviceIdentity, Pageable pageable);
    @Query(value = "SELECT t FROM task_warning t WHERE t.addTime BETWEEN ?1 AND ?2",
            countQuery = "SELECT COUNT(1) FROM task_warning WHERE add_time BETWEEN ?1 AND ?2",
            nativeQuery = true)
    List<TaskWarning> findAllByAddTime(Date beginDate, Date endDate);
}
