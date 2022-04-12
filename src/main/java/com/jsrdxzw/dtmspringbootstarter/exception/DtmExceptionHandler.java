package com.jsrdxzw.dtmspringbootstarter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/5 22:57
 */
@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DtmExceptionHandler {

    /**
     * handle defined exception
     *
     * @param req servlet request
     * @param e   exception
     * @return ResponseEntity
     */
    @ExceptionHandler(value = DtmException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bizExceptionHandler(HttpServletRequest req, DtmException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("dtm_result", e.getDtmResult().name());
        return new ResponseEntity<>(body, e.getDtmResult().getStatus());
    }
}
