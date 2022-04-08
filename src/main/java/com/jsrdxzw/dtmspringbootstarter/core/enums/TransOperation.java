package com.jsrdxzw.dtmspringbootstarter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xuzhiwei
 * @date 2022/4/5 15:08
 */
@Getter
@AllArgsConstructor
public enum TransOperation {

    /**
     * submit transaction operation
     */
    SUBMIT("submit"),

    /**
     * prepare step for tcc
     */
    PREPARE("prepare"),

    /**
     * abort global transaction
     */
    ABORT("abort"),

    /**
     * register tcc branch
     */
    REGISTER_BRANCH("registerBranch");

    private final String desc;
}
