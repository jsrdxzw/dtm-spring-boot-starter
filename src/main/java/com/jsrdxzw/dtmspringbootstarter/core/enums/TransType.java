package com.jsrdxzw.dtmspringbootstarter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xuzhiwei
 * @date 2022/4/5 14:22
 */
@Getter
@AllArgsConstructor
public enum TransType {
    /**
     * saga
     */
    SAGA("saga"),
    /**
     * tcc
     */
    TCC("tcc"),

    /**
     * two-phase commit
     */
    MSG("msg");

    private final String desc;
}
