package stu.lanyu.springdocker.config;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = true, prefix = "global.appsettings")
public class GlobalAppSettingsProperties {
    @NotBlank
    public String pwdType;
    @NotBlank
    public String privateKey;
    @NotBlank
    public String publicKey;
}
