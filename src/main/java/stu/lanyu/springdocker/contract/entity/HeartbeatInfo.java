package stu.lanyu.springdocker.contract.entity;

public class HeartbeatInfo {

    private boolean TaskVeto;
    private JobMonitorInfo[] MonitorInfos;

    public boolean isTaskVeto() {
        return TaskVeto;
    }

    public void setTaskVeto(boolean taskVeto) {
        TaskVeto = taskVeto;
    }

    public JobMonitorInfo[] getMonitorInfos() {
        return MonitorInfos;
    }

    public void setMonitorInfos(JobMonitorInfo[] monitorInfos) {
        MonitorInfos = monitorInfos;
    }
}
