package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.exception.ValidationException;
import stu.lanyu.springdocker.utility.StringUtility;

public class LoginRequest implements IValidation {
    private String passport;
    private String password;

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

    @Override
    public void Validation() throws ValidationException {
        if (StringUtility.isNullOrEmpty(this.passport)) {
            throw new ValidationException("登录账户不能为空");
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            throw new ValidationException("登录密码不能为空");
        }
    }
}
