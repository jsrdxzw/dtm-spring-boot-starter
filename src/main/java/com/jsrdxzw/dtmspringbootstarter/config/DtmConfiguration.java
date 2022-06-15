package com.jsrdxzw.dtmspringbootstarter.config;

import com.jsrdxzw.dtmspringbootstarter.core.http.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.enhancer.DtmBarrierMethodArgumentResolver;
import com.jsrdxzw.dtmspringbootstarter.enhancer.DtmResponseBodyAdvice;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * @author xuzhiwei
 * @date 2022/4/5 12:26
 */
@Import({DtmExceptionHandler.class, DtmWebMvcConfigurer.class})
public class DtmConfiguration {

    @Value("${dtm.http-server}")
    private String httpServer;

    @Bean
    public HttpClient dtmHttpClient() {
        return new HttpClient(httpServer);
    }

    @ConditionalOnMissingBean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DtmBarrierMethodArgumentResolver dtmBarrierMethodArgumentResolver() {
        return new DtmBarrierMethodArgumentResolver();
    }

    @Bean
    public DtmResponseBodyAdvice dtmResponseBodyAdvice() {
        return new DtmResponseBodyAdvice();
    }
}
