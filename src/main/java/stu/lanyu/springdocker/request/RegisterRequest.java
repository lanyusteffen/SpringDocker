package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.contract.IMapRequest;
import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.ValidationException;
import stu.lanyu.springdocker.utility.StringUtility;

import java.util.Date;

public class RegisterRequest implements IMapRequest<User>, IValidation {
    private String firstName;
    private String lastName;
    private String passport;
    private String password;
    private String pwdType;
    private String name;
    private int loginTime;
    private Date lastLoginTime;

    @Override
    public User mapToDomain() {

        User user = new User();

        user.setName(this.name);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setRegisterTime(new Date());
        user.setPassport(this.passport);
        user.setPassword(this.password);
        user.setPwdType(this.pwdType);
        user.setLastLoginTime(this.lastLoginTime);
        user.setLoginTime(this.loginTime);

        return user;
    }

    @Override
    public void Validation() throws ValidationException {
        if (StringUtility.isNullOrEmpty(this.name)) {
            throw new ValidationException("注册必须输入用户昵称");
        }
        if (StringUtility.isNullOrEmpty(this.passport)) {
            throw new ValidationException("注册必须输入用户账户");
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            throw new ValidationException("注册必须输入用户密码");
        }
        if (StringUtility.isNullOrEmpty(this.pwdType)) {
            throw new ValidationException("注册用户必须包含密码类型");
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
