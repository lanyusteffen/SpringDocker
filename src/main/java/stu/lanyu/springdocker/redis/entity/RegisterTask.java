package stu.lanyu.springdocker.redis.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class RegisterTask implements Serializable {

    private static final long serialVersionUID = -2526102990077951231L;

    private String serviceIdentity;
    private long registerTime;
    private String actionToken;
    private String BreakerUrl;
    private ArrayList<RegisterJob> registerJobs;

    public String getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public String getActionToken() {
        return actionToken;
    }

    public void setActionToken(String actionToken) {
        this.actionToken = actionToken;
    }

    public ArrayList<RegisterJob> getRegisterJobs() {
        return registerJobs;
    }

    public void setRegisterJobs(ArrayList<RegisterJob> registerJobs) {
        this.registerJobs = registerJobs;
    }

    public String getBreakerUrl() {
        return BreakerUrl;
    }

    public void setBreakerUrl(String breakerUrl) {
        BreakerUrl = breakerUrl;
    }
}
