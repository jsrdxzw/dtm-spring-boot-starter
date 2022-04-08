package com.jsrdxzw.dtmspringbootstarter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;

/**
 * @author xuzhiwei
 * @date 2022/4/6 14:41
 */
@Getter
@AllArgsConstructor
public enum BranchOperation {

    /**
     * try
     */
    BranchTry("try"),

    /**
     * confirm
     */
    BranchConfirm("confirm"),

    /**
     * cancel
     */
    BranchCancel("cancel"),

    /**
     * action
     */
    BranchAction("action"),

    /**
     * compensate
     */
    BranchCompensate("compensate"),

    /**
     * commit
     */
    BranchCommit("commit"),

    /**
     * rollback
     */
    BranchRollback("rollback"),

    /**
     * msg
     */
    BranchMSG("msg");

    private final String op;

    @Nullable
    public static BranchOperation getOriginOp(String op) {
        if (BranchCancel.getOp().equals(op)) {
            return BranchTry;
        }
        if (BranchCompensate.getOp().equals(op)) {
            return BranchAction;
        }
        return null;
    }

    public static BranchOperation fromOp(String op) {
        return Arrays.stream(values()).filter(it -> it.op.equals(op))
                .findFirst().orElseThrow(() -> new RuntimeException("can not find BranchOperation from " + op));
    }
}
