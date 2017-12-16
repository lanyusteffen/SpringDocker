package stu.lanyu.springdocker.request;

import stu.lanyu.springdocker.security.AESUtils;
import stu.lanyu.springdocker.security.RSAUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

public abstract class AbstractRequest {

    protected String encryptedPassword(String password, String privateKey, String publicKey, String pwdType) {

        String encrypedPassword = null;

        try {
            switch (pwdType) {

                case "AES":

                    SecretKey secretKey = AESUtils.convertAESKeyFromString(privateKey);
                    encrypedPassword = Base64.getEncoder().encodeToString(AESUtils.encrypt(secretKey, password.getBytes()));
                    break;

                case "RSA":

                    encrypedPassword = Base64.getEncoder().encodeToString(RSAUtils.encrypt(RSAUtils.getPublicKey(publicKey.getBytes()),
                            password.getBytes()));
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
