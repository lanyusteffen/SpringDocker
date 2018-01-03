package stu.lanyu.springdocker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "log_collect")
public class LogCollect extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 8377365962665342929L;

    @Column(columnDefinition="TEXT")
    private String body;
    private String serviceIdentity;
    private String level;
    private Date logTime;

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

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date addTime) {
        this.logTime = addTime;
    }
}
