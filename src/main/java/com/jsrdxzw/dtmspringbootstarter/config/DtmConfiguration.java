package com.jsrdxzw.dtmspringbootstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuzhiwei
 * @date 2022/4/5 12:26
 */
@Configuration
@ConfigurationProperties(prefix = "dtm")
public class DtmConfiguration {

    private String httpServer;

}
