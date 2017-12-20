package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(GlobalAppSettingsProperties.class)
public class GlobalAppSettingsConfig {
    @Autowired(required = true)
    private GlobalAppSettingsProperties globalAppSettingsProperties;

    @Bean("GlobalAppSettings")
    @Scope("singleton")
    GlobalAppSettingsProperties getGlobalAppSettings(){

       return globalAppSettingsProperties;
    }
}
