package com.jsrdxzw.dtmspringbootstarter;

import com.jsrdxzw.dtmspringbootstarter.annotations.EnableDtm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author xuzhiwei
 */
@SpringBootApplication
@EnableDtm
@EnableCaching
public class DtmSpringBootStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DtmSpringBootStarterApplication.class, args);
	}

}
