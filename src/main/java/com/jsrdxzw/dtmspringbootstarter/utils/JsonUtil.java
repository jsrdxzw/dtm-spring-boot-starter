package com.jsrdxzw.dtmspringbootstarter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author xuzhiwei
 * @date 2022/4/5 14:35
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String writeToString(Object data) {
        return MAPPER.writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T parseObject(String json, Class<T> clazz) {
        return MAPPER.readValue(json, clazz);
    }
}
