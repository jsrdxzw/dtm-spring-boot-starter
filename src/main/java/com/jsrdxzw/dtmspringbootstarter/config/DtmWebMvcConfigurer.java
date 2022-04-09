package com.jsrdxzw.dtmspringbootstarter.config;

import com.jsrdxzw.dtmspringbootstarter.enhancer.DtmBarrierMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author xuzhiwei
 * @date 2022/4/10 21:55
 */
public class DtmWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private DtmBarrierMethodArgumentResolver dtmBarrierMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(dtmBarrierMethodArgumentResolver);
    }
}
