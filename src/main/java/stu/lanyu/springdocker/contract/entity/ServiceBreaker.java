package stu.lanyu.springdocker.contract.entity;

public class ServiceBreaker {

    private String ActionToken;
    private boolean IsStartCommand;
    private boolean IsAuthenticationFailure;
    private boolean IsStopCommand;
    private String ServiceIdentity;
    private boolean BreakerResult;
    private JobBreaker[] JobBreakers;

    public String getActionToken() {
        return ActionToken;
    }

    public void setActionToken(String actionToken) {
        ActionToken = actionToken;
    }

    public boolean isStartCommand() {
        return IsStartCommand;
    }

    public void setStartCommand(boolean startCommand) {
        IsStartCommand = startCommand;
    }

    public boolean isAuthenticationFailure() {
        return IsAuthenticationFailure;
    }

    public void setAuthenticationFailure(boolean authenticationFailure) {
        IsAuthenticationFailure = authenticationFailure;
    }

    public boolean isStopCommand() {
        return IsStopCommand;
    }

    public void setStopCommand(boolean stopCommand) {
        IsStopCommand = stopCommand;
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
