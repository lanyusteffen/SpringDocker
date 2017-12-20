package stu.lanyu.springdocker.utility;

import io.jsonwebtoken.*;
import stu.lanyu.springdocker.RunnerContext;
import stu.lanyu.springdocker.config.GlobalAppSettingsProperties;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.response.AuthToken;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtility {

    public static String createJWT(String id, long userId, String role,
                                    long ttlMillis) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecret());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(GlobalConfig.JWTConfig.SUBJECT)
                .setIssuer(GlobalConfig.JWTConfig.IISUSER)
                .signWith(signatureAlgorithm, signingKey);

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
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

    public static AuthToken createAuthToken(long userId, String role) {

        AuthToken tokens = new AuthToken();

        tokens.setAccessToken(createJWT(GlobalConfig.JWTConfig.JWTID, userId, role,
                GlobalConfig.JWTConfig.TTLMILLIS));
        tokens.setRefreshToken(createJWT(GlobalConfig.JWTConfig.JWTREFRESHID, userId, role,
                GlobalConfig.JWTConfig.REFRESHTTLMILLIS));

        return tokens;
    }

    public static Jws<Claims> parseRefreshJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Jws<Claims> claims = Jwts.parser()
                .requireId(GlobalConfig.JWTConfig.JWTREFRESHID)
                .requireSubject(GlobalConfig.JWTConfig.SUBJECT)
                .requireIssuer(GlobalConfig.JWTConfig.IISUSER)
                .setSigningKey(DatatypeConverter.parseBase64Binary(getSecret()))
                .parseClaimsJws(jwt);

        return claims;
    }

    public static Jws<Claims> parseJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Jws<Claims> claims = Jwts.parser()
                .requireId(GlobalConfig.JWTConfig.JWTID)
                .requireSubject(GlobalConfig.JWTConfig.SUBJECT)
                .requireIssuer(GlobalConfig.JWTConfig.IISUSER)
                .setSigningKey(DatatypeConverter.parseBase64Binary(getSecret()))
                .parseClaimsJws(jwt);

        return claims;
    }

    private static String getSecret() {

        GlobalAppSettingsProperties globalAppSettingsProperties =
                RunnerContext.getBean("GlobalAppSettings", GlobalAppSettingsProperties.class);
        return globalAppSettingsProperties.getPrivateKey();
    }
}
