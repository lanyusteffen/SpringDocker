package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.JobMonitorInfo;
import stu.lanyu.springdocker.repository.readwrite.JobMonitorInfoRepository;

import java.util.ArrayList;
import java.util.List;

@Service("JobMonitorInfoServiceReadwrite")
public class JobMonitorInfoService {

    @Autowired(required = true)
    @Qualifier("JobMonitorInfoRepositoryReadwrite")
    private JobMonitorInfoRepository jobMonitorInfoRepository;

    public void save(JobMonitorInfo jobMonitorInfo) {
        jobMonitorInfoRepository.save(jobMonitorInfo);
    }

    public void delete(List<JobMonitorInfo> monitorInfoList) {

        jobMonitorInfoRepository.delete(monitorInfoList);
    }
}
