package stu.lanyu.springdocker.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "readWriteDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.readwrite")
    @Primary
    public DataSource readWriteDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "readOnlyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.readonly")
    public DataSource readOnlyDataSource(){
        return DataSourceBuilder.create().build();
    }
}
