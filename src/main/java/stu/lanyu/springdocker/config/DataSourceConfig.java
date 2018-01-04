package stu.lanyu.springdocker.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "readWriteDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.readwrite")
    @Primary
    public DataSource readWriteDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "readOnlyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.readonly")
    public DataSource readOnlyDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}
