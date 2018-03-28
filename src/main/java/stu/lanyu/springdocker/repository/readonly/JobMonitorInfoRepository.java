package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.entity.JobMonitorInfo;

@Repository("JobMonitorInfoRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface JobMonitorInfoRepository extends JpaRepository<JobMonitorInfo, Long> {
    Page<JobMonitorInfo> findAllByJobNameAndJobGroup(String jobName, String jobGroup, Pageable pageable);
    Page<JobMonitorInfo> findAllByServiceIdentity(String serviceIdentity, Pageable pageable);
}
