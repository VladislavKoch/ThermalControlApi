package ru.vladkochur.thermalControlApi.it.testJDBC;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static ru.vladkochur.thermalControlApi.it.testcontainers.AbstractRestControllerBaseTest.POSTGRE_SQL_CONTAINER;


@TestConfiguration
public class Test_JdbcConfig {
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}


