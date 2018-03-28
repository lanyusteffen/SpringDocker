package stu.lanyu.springdocker.domain.valueobject;

public class JobBreaker {

    private boolean JobVeto;
    private String JobName;
    private String JobGroup;
    private boolean BreakerResult;

    public boolean isJobVeto() {
        return JobVeto;
    }

    public void setJobVeto(boolean jobVeto) {
        JobVeto = jobVeto;
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
