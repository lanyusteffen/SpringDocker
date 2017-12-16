package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;
import stu.lanyu.springdocker.utility.DateUtility;
import stu.lanyu.springdocker.utility.StringUtility;

public class LoginRequest extends AbstractRequest implements IValidation {

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
    public void validation() throws DomainException {
        ValidationErrors errors = new ValidationErrors();
        if (StringUtility.isNullOrEmpty(this.passport)) {
            errors.getErrorItems().add(new ValidationError("Passport", null));
            throw new DomainException("登录名不能为空", errors);
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            errors.getErrorItems().add(new ValidationError("Password", null));
            throw new DomainException("登录密码不能为空", errors);
        }
    }

    public void makePasswordSecurity(User user) {

        try {
            user.setPassword(encryptedPassword(this.password, user.getPrivateKey(), user.getPublicKey(), user.getPwdType()));
        }
        catch (Exception ex) {
            System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                    "LoginRequest encrypt type '" + user.getPwdType() + "' occur error: " + ex.getMessage());
            user.setPassword(this.password);
        }
    }
}
