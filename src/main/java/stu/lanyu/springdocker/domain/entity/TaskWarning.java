package stu.lanyu.springdocker.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import stu.lanyu.springdocker.domain.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "task_warning")
public class TaskWarning extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -5343683171508755925L;

    private String serviceIdentity;
    private String jobName;
    private String jobGroup;
    private String warningReason;
    private Date addTime;

    public String getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
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

    public String getWarningReason() {
        return warningReason;
    }

    public void setWarningReason(String warningReason) {
        this.warningReason = warningReason;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
