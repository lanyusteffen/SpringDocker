package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.security.AESUtils;
import stu.lanyu.springdocker.security.RSAUtils;

import javax.crypto.SecretKey;

public abstract class AbstractRequest {

    public abstract void makePasswordSecurity(User user, String privateKey, String publicKey, String pwdType);

    protected String encryptedPassword(String password, String privateKey, String publicKey, String pwdType) {

        String encrypedPassword = null;

        try {
            switch (pwdType) {

                case "AES":

                    SecretKey secretKey = AESUtils.convertAESKeyFromString(privateKey);
                    encrypedPassword = new String(AESUtils.encrypt(secretKey, password.getBytes()));
                    break;

                case "RSA":

                    encrypedPassword = new String(RSAUtils.encrypt(RSAUtils.getPublicKey(publicKey.getBytes()), password.getBytes()));
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
