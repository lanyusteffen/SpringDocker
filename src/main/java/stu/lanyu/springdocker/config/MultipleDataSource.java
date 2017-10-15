package stu.lanyu.springdocker.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 获得数据源
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DbContextHolder.getDbType();
    }
}
