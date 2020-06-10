package com.example.schemabuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSources {
    @ConfigurationProperties(prefix = "spring.datasource.mydb")
    @Bean
    public DataSource mydb() {
        System.out.println("DB Connected");
        return DataSourceBuilder.create().build();
    }
}
