package com.example.schemabuilder;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("com.example.schemabuilder")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Autowired
    Environment environment;

    private final String URL = "spring.datasource.mydb.jdbc-url";
    private final String USER = "spring.datasource.mydb.username";
    private final String DRIVER = "spring.datasource.mydb.driver-class-name";
    private final String PASSWORD = "spring.datasource.mydb.password";

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty(URL));
        driverManagerDataSource.setUsername(environment.getProperty(USER));
        driverManagerDataSource.setPassword(environment.getProperty(PASSWORD));
        driverManagerDataSource.setDriverClassName(environment.getProperty(DRIVER));
        return driverManagerDataSource;
    }

}
