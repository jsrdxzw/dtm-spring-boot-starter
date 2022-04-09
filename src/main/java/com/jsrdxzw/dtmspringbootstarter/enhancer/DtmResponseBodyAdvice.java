package com.jsrdxzw.dtmspringbootstarter.enhancer;

import com.jsrdxzw.dtmspringbootstarter.annotations.DtmResponse;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.TransactionResponse;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/9 12:12
 */
public class DtmResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(DtmResponse.class);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null || body instanceof TransactionResponse) {
            return body;
        }
        if (ClassUtils.isPrimitiveOrWrapper(body.getClass()) || body instanceof String) {
            throw new RuntimeException("DtmResponse must return java pojo class type, not primitiveOrWrapper type");
        }
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("dtm_result", DtmResultEnum.SUCCESS.getDesc());
        return ReflectUtil.getObject(body, propertiesMap);
    }
}