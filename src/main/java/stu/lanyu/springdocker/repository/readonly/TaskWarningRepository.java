package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.TaskWarning;
import stu.lanyu.springdocker.domain.User;

@Repository("TaskWarningRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface TaskWarningRepository extends JpaRepository<TaskWarning, Long> {
    Page<TaskWarning> findAllByJobNameAndJobGroup(String jobName, String jobGroup, Pageable pageable);
    Page<TaskWarning> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
}
