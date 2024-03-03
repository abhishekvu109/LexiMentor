//package com.abhi.leximentor.inventory.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//
//
//@Configuration
//@Slf4j
//public class LogDatasourceConfig {
//
//
//    @Bean(name = "dataSource1")
//    @ConfigurationProperties(prefix = "spring.datasource.datasource1")
//    public DataSource getDatasource() {
//        return DataSourceBuilder.create().build();
//    }
//
//
//    @Bean(name = "jdbcTemplate1")
//    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource1") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
//}
