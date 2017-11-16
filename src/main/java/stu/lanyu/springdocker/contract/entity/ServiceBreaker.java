package stu.lanyu.springdocker.contract.entity;

public class ServiceBreaker {

    private String ActionToken;
    private boolean TaskVeto;
    private boolean IsAuthenticationFailure;
    private String ServiceIdentity;
    private boolean BreakerResult;
    private JobBreaker[] JobBreakers;
    private boolean BreakerForTask;

    public boolean isBreakerForTask() {
        return BreakerForTask;
    }

    public void setBreakerForTask(boolean breakerForTask) {
        BreakerForTask = breakerForTask;
    }

    public String getActionToken() {
        return ActionToken;
    }

    public void setActionToken(String actionToken) {
        ActionToken = actionToken;
    }

    public boolean isTaskVeto() {
        return TaskVeto;
    }

    public void setTaskVeto(boolean taskVeto) {
        TaskVeto = taskVeto;
    }

    public boolean isAuthenticationFailure() {
        return IsAuthenticationFailure;
    }

    public void setAuthenticationFailure(boolean authenticationFailure) {
        IsAuthenticationFailure = authenticationFailure;
    }

    public String getServiceIdentity() {
        return ServiceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        ServiceIdentity = serviceIdentity;
    }

    public boolean isBreakerResult() {
        return BreakerResult;
    }

    public void setBreakerResult(boolean breakerResult) {
        BreakerResult = breakerResult;
    }

    public JobBreaker[] getJobBreakers() {
        return JobBreakers;
    }

    public void setJobBreakers(JobBreaker[] jobBreakers) {
        JobBreakers = jobBreakers;
    }
}
