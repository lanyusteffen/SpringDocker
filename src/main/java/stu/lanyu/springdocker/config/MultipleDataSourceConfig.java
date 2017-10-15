package stu.lanyu.springdocker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultipleDataSourceConfig {

    @Autowired(required = true)
    @Qualifier("readWriteDataSource")
    private DataSource readWriteDataSource;

    @Autowired(required = true)
    @Qualifier("readOnlyDataSource")
    private DataSource readOnlyDataSource;

    @Bean
    public MultipleDataSource dataSource(){

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DbType.MASTER, readWriteDataSource);
        targetDataSources.put(DbType.SLAVE, readOnlyDataSource);

        MultipleDataSource multipleDataSource = new MultipleDataSource();

        //设置数据源映射
        multipleDataSource.setTargetDataSources(targetDataSources);

        //设置默认数据源，当无法映射到数据源时会使用默认数据源
        multipleDataSource.setDefaultTargetDataSource(readWriteDataSource);

        multipleDataSource.afterPropertiesSet();

        return multipleDataSource;
    }
}
