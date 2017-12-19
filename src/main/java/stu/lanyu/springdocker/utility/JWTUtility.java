package stu.lanyu.springdocker.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import stu.lanyu.springdocker.config.GlobalAppSettingsProperties;
import stu.lanyu.springdocker.config.GlobalConfig;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtility {

    public static String createJWT(long userId, String role) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecret());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(GlobalConfig.JWTConfig.id)
                .setIssuedAt(now)
                .setSubject(GlobalConfig.JWTConfig.subject)
                .setIssuer(GlobalConfig.JWTConfig.issuser)
                .signWith(signatureAlgorithm, signingKey);

        // if it has been specified, let's add the expiration
        if (GlobalConfig.JWTConfig.ttlMillis >= 0) {
            long expMillis = nowMillis + GlobalConfig.JWTConfig.ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        Map<String, Object> header = new HashMap<String, Object>();

        header.put(GlobalConfig.WebConfig.CLAIMS_USER_KEY, userId);
        header.put(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY, role);

        builder.setHeader(header);

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims parseJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .requireSubject(GlobalConfig.JWTConfig.subject)
                .requireIssuer(GlobalConfig.JWTConfig.issuser)
                .setSigningKey(DatatypeConverter.parseBase64Binary(getSecret()))
                .parseClaimsJws(jwt).getBody();

        return claims;
    }

    @Autowired(required = true)
    private static ApplicationContext context;

    private static String getSecret() {

        GlobalAppSettingsProperties globalAppSettingsProperties =
                context.getBean("GlobalAppSettings", GlobalAppSettingsProperties.class);
        return globalAppSettingsProperties.getPrivateKey();
    }
}
