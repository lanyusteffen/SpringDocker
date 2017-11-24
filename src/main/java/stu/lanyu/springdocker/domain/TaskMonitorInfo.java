package stu.lanyu.springdocker.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "task_monitor_info", uniqueConstraints={
        @UniqueConstraint(columnNames = {"id", "service_identity"})})
public class TaskMonitorInfo extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -1204482456956189973L;
    private boolean heartbeatBreak;
    private boolean taskVeto;
    private String heartbeatUrl;
    private Date lastHeartbeatTime;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
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

    public String isHeartbeatUrl() {
        return heartbeatUrl;
    }

    public void setHeartbeatUrl(String heartbeatUrl) {
        this.heartbeatUrl = heartbeatUrl;
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
