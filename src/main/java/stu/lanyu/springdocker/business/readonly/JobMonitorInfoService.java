package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.JobMonitorInfo;
import stu.lanyu.springdocker.repository.readonly.JobMonitorInfoRepository;

@Service("JobMonitorInfoServiceReadonly")
public class JobMonitorInfoService {

    @Autowired(required = true)
    @Qualifier("JobMonitorInfoRepositoryReadonly")
    private JobMonitorInfoRepository jobMonitorInfoRepository;

    public Page<JobMonitorInfo> getListPaged(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return jobMonitorInfoRepository.findAll(pageable);
    }

    public Page<JobMonitorInfo> getListPagedByServiceIdentity(String serviceIdentity, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return jobMonitorInfoRepository.findAllByServiceIdentity(serviceIdentity, pageable);
    }

    public Page<JobMonitorInfo> getListPagedByJob(String jobName, String jobGroup, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.Direction.ASC, "id");
        return jobMonitorInfoRepository.findAllByJobNameAndJobGroup(jobName, jobGroup, pageable);
    }
}
