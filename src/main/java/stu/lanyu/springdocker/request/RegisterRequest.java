package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.RunnerContext;
import stu.lanyu.springdocker.config.GlobalAppSettingsProperties;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.IMapRequest;
import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.domain.entity.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;
import stu.lanyu.springdocker.utility.DateUtility;
import stu.lanyu.springdocker.utility.StringUtility;

import java.util.Date;

public class RegisterRequest extends AbstractRequest implements IMapRequest<User>, IValidation {

    private String firstName;
    private String lastName;
    private String passport;
    private String password;
    private String nickName;

    public void makePasswordSecurity(User user) {

        GlobalAppSettingsProperties properties = (GlobalAppSettingsProperties)RunnerContext
                .getBean("GlobalAppSettings");

        String pwdType = properties.getPwdType();
        String privateKey = properties.getPrivateKey();
        String publicKey = properties.getPublicKey();

        try {

            switch (pwdType) {

                case "AES":

                    user.setPrivateKey(privateKey);
                    user.setPassword(encryptedPassword(this.password, privateKey, null, pwdType));

                    if (user.getPassword() == this.password) {
                        user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    } else {
                        user.setPwdType(pwdType);
                    }
                    break;

                case "RSA":

                    user.setPrivateKey(privateKey);
                    user.setPublicKey(publicKey);
                    user.setPassword(encryptedPassword(this.password, privateKey, publicKey, pwdType));

                    if (user.getPassword() == this.password) {
                        user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    } else {
                        user.setPwdType(pwdType);
                    }
                    break;

                default:

                    user.setPassword(this.password);
                    user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    break;
            }
        }
        catch (Exception ex) {
            System.out.println("[" + DateUtility.getDateNowFormat(null) + "]" +
                    "RegisterRequest encrypt type '" + pwdType + "' occur error: " + ex.getMessage());
            user.setPassword(this.password);
            user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
        }
    }

    @Override
    public User mapToDomain() {

        User user = new User();
        user.setNickName(this.nickName);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setRegisterTime(new Date());
        user.setPassport(this.passport);
        user.setAuditToUse(false);
        return user;
    }

    @Override
    public void validation() throws DomainException {

        ValidationErrors errors = new ValidationErrors();

        if (StringUtility.isNullOrEmpty(this.nickName)) {
            errors.getErrorItems().add(new ValidationError("NickName", null));
            throw new DomainException("用户昵称不能为空", errors);
        }
        if (StringUtility.isNullOrEmpty(this.passport)) {
            errors.getErrorItems().add(new ValidationError("Passport", null));
            throw new DomainException("登录名不能为空", errors);
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            errors.getErrorItems().add(new ValidationError("Password", null));
            throw new DomainException("登录密码不能为空", errors);
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
