package com.jsrdxzw.dtmspringbootstarter.enhancer;

import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/9 15:42
 */
public class DynamicBean {
    private final Object target;

    private final BeanMap beanMap;

    public DynamicBean(Class superclass, Map<String, Class> propertyMap) {
        this.target = generateBean(superclass, propertyMap);
        this.beanMap = BeanMap.create(this.target);
    }

    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    public Object getValue(String property) {
        return beanMap.get(property);
    }

    public Object getTarget() {
        return this.target;
    }

    /**
     * generate class from properties
     */
    private Object generateBean(Class superclass, Map<String, Class> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        if (null != superclass) {
            generator.setSuperclass(superclass);
        }
        BeanGenerator.addProperties(generator, propertyMap);
        return generator.create();
    }
}
