package stu.lanyu.springdocker.contract.entity;

public class JobMonitorInfo {

    private boolean IsVeto;
    private String JobName;
    private String JobGroup;
    private int FiredTimes;
    private int MissfireTimes;
    private long JobFiredLastTime;
    private long JobCompletedLastTime;
    private long JobMissfireLastTime;

    public boolean isVeto() {
        return IsVeto;
    }

    public void setVeto(boolean veto) {
        IsVeto = veto;
    }

    public String getJobName() {
        return JobName;
    }

    public void setJobName(String jobName) {
        JobName = jobName;
    }

    public String getJobGroup() {
        return JobGroup;
    }

    public void setJobGroup(String jobGroup) {
        JobGroup = jobGroup;
    }

    public int getFiredTimes() {
        return FiredTimes;
    }

    public void setFiredTimes(int firedTimes) {
        FiredTimes = firedTimes;
    }

    public int getMissfireTimes() {
        return MissfireTimes;
    }

    public void setMissfireTimes(int missfireTimes) {
        MissfireTimes = missfireTimes;
    }

    public long getJobFiredLastTime() {
        return JobFiredLastTime;
    }

    public void setJobFiredLastTime(long jobFiredLastTime) {
        JobFiredLastTime = jobFiredLastTime;
    }

    public long getJobCompletedLastTime() {
        return JobCompletedLastTime;
    }

    public void setJobCompletedLastTime(long jobCompletedLastTime) {
        JobCompletedLastTime = jobCompletedLastTime;
    }

    public long getJobMissfireLastTime() {
        return JobMissfireLastTime;
    }

    public void setJobMissfireLastTime(long jobMissfireLastTime) {
        JobMissfireLastTime = jobMissfireLastTime;
    }
}
