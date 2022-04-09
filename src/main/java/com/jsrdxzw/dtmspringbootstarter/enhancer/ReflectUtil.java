package com.jsrdxzw.dtmspringbootstarter.enhancer;

import lombok.SneakyThrows;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/9 15:43
 */
public class ReflectUtil {

    @SneakyThrows
    public static Object getObject(Object dest, Map<String, Object> newValueMap) {

        // 1. get origin target class properties
        PropertyDescriptor[] descriptorArr = Introspector.getBeanInfo(dest.getClass()).getPropertyDescriptors();

        // put field properties into Map
        Map<String, Class> oldKeyMap = new HashMap<>();
        for (PropertyDescriptor it : descriptorArr) {
            if (!"class".equalsIgnoreCase(it.getName())) {
                oldKeyMap.put(it.getName(), it.getPropertyType());
                newValueMap.put(it.getName(), it.getPropertyType());
            }
        }


        newValueMap.forEach((k, v) -> oldKeyMap.putIfAbsent(k, v.getClass()));

        DynamicBean dynamicBean = new DynamicBean(dest.getClass(), oldKeyMap);

        newValueMap.forEach((k, v) -> {
            try {
                dynamicBean.setValue(k, v);
            } catch (Exception ignored) {
            }
        });
        return dynamicBean.getTarget();
    }
}
