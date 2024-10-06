//package com.abhi.writewise.inventory.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "com.abhi.writewise.inventory.repository.sql.mysql", // Package for MySQL repositories
//        entityManagerFactoryRef = "mysqlEntityManagerFactory", transactionManagerRef = "mysqlTransactionManager")
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class MySQLConfig {
//
//    private final DataSource dataSource;
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//        return builder.dataSource(dataSource).packages("com.abhi.writewise.inventory.entities.sql.mysql").persistenceUnit("mysql").build();
//    }
//
//    @Bean
//    public PlatformTransactionManager mysqlTransactionManager(LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(mysqlEntityManagerFactory.getObject());
//        return transactionManager;
//    }
//}
