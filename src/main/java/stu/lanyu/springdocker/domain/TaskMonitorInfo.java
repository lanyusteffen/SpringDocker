package stu.lanyu.springdocker.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "task_monitor_info")
public class TaskMonitorInfo extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -1204482456956189973L;
    private boolean heartbeatBreak;
    private boolean isVeto;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "service_identity", referencedColumnName = "service_identity")
    @OrderBy("service_identity ASC")
    private List<JobMonitorInfo> jobs;

    @Column(unique = true, name = "service_identity")
    private String serviceIdentity;

    public boolean isVeto() {
        return isVeto;
    }

    public void setVeto(boolean veto) {
        isVeto = veto;
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
