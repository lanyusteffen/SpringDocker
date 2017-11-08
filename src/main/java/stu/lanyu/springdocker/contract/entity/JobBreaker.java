package stu.lanyu.springdocker.contract.entity;

public class JobBreaker {

    private boolean IsStartCommand;
    private boolean IsStopCommand;
    private String JobName;
    private String JobGroup;
    private boolean BreakerResult;

    public boolean isStartCommand() {
        return IsStartCommand;
    }

    public void setStartCommand(boolean startCommand) {
        IsStartCommand = startCommand;
    }

    public boolean isStopCommand() {
        return IsStopCommand;
    }

    public void setStopCommand(boolean stopCommand) {
        IsStopCommand = stopCommand;
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

    public boolean isBreakerResult() {
        return BreakerResult;
    }

    public void setBreakerResult(boolean breakerResult) {
        BreakerResult = breakerResult;
    }
}
