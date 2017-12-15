package stu.lanyu.springdocker.request;

import org.springframework.beans.factory.annotation.Autowired;
import stu.lanyu.springdocker.config.GlobalAppSettingsProperties;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.security.AESUtils;
import stu.lanyu.springdocker.security.RSAUtils;

import javax.crypto.SecretKey;

public abstract class AbstractRequest {

    @Autowired(required = true)
    protected GlobalAppSettingsProperties globalAppSettingsProperties;

    public abstract void makePasswordSecurity(User user);

    protected String encryptedPassword(String password, String privateKey, String publicKey) {

        String encrypedPassword = null;

        try {
            switch (globalAppSettingsProperties.pwdType) {

                case "AES":

                    SecretKey secretKey = AESUtils.convertAESKeyFromString(privateKey);
                    encrypedPassword = AESUtils.encrypt(secretKey, password.getBytes()).toString();
                    break;

                case "RSA":

                    encrypedPassword = RSAUtils.encrypt(RSAUtils.getPublicKey(publicKey.getBytes()), password.getBytes()).toString();
                    break;

                default:

                    encrypedPassword = password;
                    break;
            }
        }
        catch (Exception ex) {
            encrypedPassword = password;
        }

        return encrypedPassword;
    }
}
