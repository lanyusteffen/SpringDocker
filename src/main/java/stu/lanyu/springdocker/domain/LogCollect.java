package stu.lanyu.springdocker.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "log_collect")
public class LogCollect extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 8377365962665342929L;

    private String body;
    private String serviceIdentity;
    private String level;
    private Date addTime;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getServiceIdentity() {
        return serviceIdentity;
    }

    public void setServiceIdentity(String serviceIdentity) {
        this.serviceIdentity = serviceIdentity;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String category) {
        this.level = category;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
