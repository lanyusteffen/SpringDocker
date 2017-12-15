package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.contract.IMapRequest;
import stu.lanyu.springdocker.contract.IValidation;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;
import stu.lanyu.springdocker.security.AESUtils;
import stu.lanyu.springdocker.security.RSAUtils;
import stu.lanyu.springdocker.utility.StringUtility;

import javax.crypto.SecretKey;
import java.util.Date;

public class RegisterRequest extends AbstractRequest implements IMapRequest<User>, IValidation {

    private String firstName;
    private String lastName;
    private String passport;
    private String password;
    private String nickName;

    public void makePasswordSecurity(User user) {

        try {

            switch (globalAppSettingsProperties.pwdType) {

                case "AES":

                    SecretKey secretKey = AESUtils.generateKey();
                    user.setPrivateKey(AESUtils.convertAESKeyToString(secretKey));
                    user.setPassword(encryptedPassword(this.password, user.getPrivateKey(), null));

                    if (user.getPassword() == this.password) {
                        user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    } else {
                        user.setPwdType(globalAppSettingsProperties.pwdType);
                    }
                    break;

                case "RSA":

                    user.setPassword(RSAUtils.encrypt(RSAUtils.getPublicKey(globalAppSettingsProperties.publicKey.getBytes()), this.password.getBytes()).toString());
                    user.setPrivateKey(globalAppSettingsProperties.privateKey);
                    user.setPublicKey(globalAppSettingsProperties.publicKey);

                    if (user.getPassword() == this.password) {
                        user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    } else {
                        user.setPwdType(globalAppSettingsProperties.pwdType);
                    }
                    break;

                default:

                    user.setPassport(this.passport);
                    user.setPwdType(GlobalConfig.WebConfig.PASSWORD_NOSECURITY);
                    break;
            }
        }
        catch (Exception ex) {
            user.setPassport(this.passport);
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
        makePasswordSecurity(user);
        return user;
    }

    @Override
    public void validation() throws DomainException {
        ValidationErrors errors = new ValidationErrors();
        if (StringUtility.isNullOrEmpty(this.nickName)) {
            errors.getErrorItems().add(new ValidationError("NickName", "用户昵称不能为空", null));
            throw new DomainException("注册失败: ", errors);
        }
        if (StringUtility.isNullOrEmpty(this.passport)) {
            errors.getErrorItems().add(new ValidationError("Passport", "登录名不能为空", null));
            throw new DomainException("注册失败: ", errors);
        }
        if (StringUtility.isNullOrEmpty(this.password)) {
            errors.getErrorItems().add(new ValidationError("Password", "登录密码不能为空", null));
            throw new DomainException("注册失败: ", errors);
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
