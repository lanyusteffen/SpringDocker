package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;
import stu.lanyu.springdocker.security.RSAUtils;
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
            errors.getErrorItems().add(new ValidationError("Passport", "登录名不能为空", null));
            throw new DomainException("登录失败: ", errors);
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            errors.getErrorItems().add(new ValidationError("Password", "登录密码不能为空", null));
            throw new DomainException("登录失败: ", errors);
        }
    }

    @Override
    public void makePasswordSecurity(User user) {

        try {

            switch (globalAppSettingsProperties.pwdType) {

                case "AES":

                    user.setPassword(encryptedPassword(this.password, user.getPrivateKey(), null));
                    break;

                case "RSA":

                    user.setPassword(RSAUtils.encrypt(RSAUtils.getPublicKey(user.getPublicKey().getBytes()), this.password.getBytes()).toString());
                    break;

                default:

                    user.setPassport(this.passport);
                    break;
            }
        }
        catch (Exception ex) {
            user.setPassport(this.passport);
        }
    }
}
