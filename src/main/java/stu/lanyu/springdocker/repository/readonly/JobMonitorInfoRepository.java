package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readonly;
import stu.lanyu.springdocker.domain.entity.JobMonitorInfo;

@Repository("JobMonitorInfoRepositoryReadonly")
@Readonly
@Transactional(readOnly = true)
public interface JobMonitorInfoRepository extends JpaRepository<JobMonitorInfo, Long> {
    Page<JobMonitorInfo> findAllByJobNameAndJobGroup(String jobName, String jobGroup, Pageable pageable);
    Page<JobMonitorInfo> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
}
