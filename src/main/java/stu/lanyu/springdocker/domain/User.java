package stu.lanyu.springdocker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_account")
public class User extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -202216414794259675L;

    @Column(nullable = false)
    private String name;
    private String firstName;
    private String lastName;
    private String password;
    @Column(nullable = false)
    private String pwdType;
    private int loginTime;
    private Date lastLoginTime;
    private Date registerTime;

    @Column(unique = true, nullable = false)
    private String passport;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwdType() {
        return pwdType;
    }

    public void setPwdType(String pwdType) {
        this.pwdType = pwdType;
    }

    public int getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(int loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
