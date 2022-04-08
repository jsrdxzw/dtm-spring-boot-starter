package com.jsrdxzw.dtmspringbootstarter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author xuzhiwei
 * @date 2022/4/6 11:39
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dtm.sql")
public class DtmJdbcConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean
    public DataSource dtmJdbcDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}