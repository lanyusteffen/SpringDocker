package stu.lanyu.springdocker.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import stu.lanyu.springdocker.domain.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "task_monitor_info", uniqueConstraints={
        @UniqueConstraint(columnNames = {"id", "service_identity"})})
public class TaskMonitorInfo extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -1204482456956189973L;
    private boolean heartbeatBreak;
    private boolean taskVeto;
    private String heartbeatUrl;
    private String breakerUrl;
    private String actionToken;
    private Date registerTime;
    private Date lastHeartbeatTime;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.EAGER)
    @JoinColumn(name = "service_identity", referencedColumnName = "service_identity")
    @OrderBy("service_identity ASC")
    private List<JobMonitorInfo> jobs;

    @Column(name = "service_identity")
    private String serviceIdentity;

    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public boolean isTaskVeto() {
        return taskVeto;
    }

    public void setTaskVeto(boolean taskVeto) {
        this.taskVeto = taskVeto;
    }

    public String getHeartbeatUrl() {
        return heartbeatUrl;
    }

    public void setHeartbeatUrl(String heartbeatUrl) {
        this.heartbeatUrl = heartbeatUrl;
    }

    public String getBreakerUrl() {
        return breakerUrl;
    }

    public void setBreakerUrl(String breakerUrl) {
        this.breakerUrl = breakerUrl;
    }

    public String getActionToken() {
        return actionToken;
    }

    public void setActionToken(String actionToken) {
        this.actionToken = actionToken;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
    }

    public boolean isHeartbeatBreak() {
        return heartbeatBreak;
    }

    public void setHeartbeatBreak(boolean heartbeatBreak) {
        this.heartbeatBreak = heartbeatBreak;
    }

    public List<JobMonitorInfo> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobMonitorInfo> jobs) {
        this.jobs = jobs;
    }
}
