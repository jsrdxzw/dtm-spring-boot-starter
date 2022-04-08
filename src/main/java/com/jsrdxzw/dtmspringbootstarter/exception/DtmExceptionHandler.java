package com.jsrdxzw.dtmspringbootstarter.exception;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@ControllerAdvice
public class DtmExceptionHandler {

    /**
     * handle defined exception
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = DtmException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bizExceptionHandler(HttpServletRequest req, DtmException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("dtm_result", e.getDtmResult().name());
        return new ResponseEntity<>(body, e.getDtmResult().getStatus());
    }
}
