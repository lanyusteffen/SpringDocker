package stu.lanyu.springdocker.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import stu.lanyu.springdocker.domain.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "job_monitor_info", uniqueConstraints={
        @UniqueConstraint(columnNames = {"id", "service_identity", "jobName", "jobGroup"})})
public class JobMonitorInfo extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 3710843911288040526L;

    private boolean jobVeto;
    private String jobName;
    private String jobGroup;

    @Column(name = "service_identity")
    private String serviceIdentity;

    private int firedTimes;
    private int missfireTimes;
    private Date jobFiredLastTime;
    private Date jobCompletedLastTime;
    private Date jobMissfiredLastTime;

    public boolean isJobVeto() {
        return jobVeto;
    }

    public void setJobVeto(boolean jobVeto) {
        this.jobVeto = jobVeto;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public int getFiredTimes() {
        return firedTimes;
    }

    public void setFiredTimes(int firedTimes) {
        this.firedTimes = firedTimes;
    }

    public int getMissfireTimes() {
        return missfireTimes;
    }

    public void setMissfireTimes(int missfireTimes) {
        this.missfireTimes = missfireTimes;
    }

    public Date getJobFiredLastTime() {
        return jobFiredLastTime;
    }

    public void setJobFiredLastTime(Date jobFiredLastTime) {
        this.jobFiredLastTime = jobFiredLastTime;
    }

    public Date getJobCompletedLastTime() {
        return jobCompletedLastTime;
    }

    public void setJobCompletedLastTime(Date jobCompletedLastTime) {
        this.jobCompletedLastTime = jobCompletedLastTime;
    }

    public Date getJobMissfiredLastTime() {
        return jobMissfiredLastTime;
    }

    public void setJobMissfiredLastTime(Date jobMissfiredLastTime) {
        this.jobMissfiredLastTime = jobMissfiredLastTime;
    }

    public String getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
    }
}
