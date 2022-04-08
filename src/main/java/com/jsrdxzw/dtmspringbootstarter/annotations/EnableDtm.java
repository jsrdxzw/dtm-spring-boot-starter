package com.jsrdxzw.dtmspringbootstarter.annotations;

import com.jsrdxzw.dtmspringbootstarter.config.DtmConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xuzhiwei
 * @date 2022/4/5 12:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DtmConfiguration.class})
public @interface EnableDtm {
}