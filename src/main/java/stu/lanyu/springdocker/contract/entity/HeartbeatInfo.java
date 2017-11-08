package stu.lanyu.springdocker.contract.entity;

public class HeartbeatInfo {

    private boolean IsVetoForTask;
    private JobMonitorInfo[] MonitorInfos;

    public boolean isVetoForTask() {
        return IsVetoForTask;
    }

    public void setVetoForTask(boolean vetoForTask) {
        IsVetoForTask = vetoForTask;
    }

    public JobMonitorInfo[] getMonitorInfos() {
        return MonitorInfos;
    }

    public void setMonitorInfos(JobMonitorInfo[] monitorInfos) {
        MonitorInfos = monitorInfos;
    }
}
