package stu.lanyu.springdocker.domain;

import java.io.Serializable;

public class RegisterJob implements Serializable {

    private static final long serialVersionUID = 6973185978073821853L;

    private String jobName;
    private String jobGroup;
    private String jobClassType;
    private String triggerName;
    private String triggerGroup;
    private int repeatCount;
    private int repeatInterval;

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

    public String getJobClassType() {
        return jobClassType;
    }

    public void setJobClassType(String jobClassType) {
        this.jobClassType = jobClassType;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
