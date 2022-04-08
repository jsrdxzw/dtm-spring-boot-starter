package com.jsrdxzw.dtmspringbootstarter.core;

import lombok.Data;

/**
 * @author xuzhiwei
 * @date 2022/4/5 14:13
 */
@Data
public class BranchIDGen {

    private String branchId;

    private Integer subBranchId = 0;

    public String newSubBranchId() {
        if (subBranchId >= 99) {
            throw new RuntimeException("branch id is larger than 99");
        }
        if (branchId.length() >= 20) {
            throw new RuntimeException("total branch id is longer than 20");
        }
        subBranchId++;
        return currentSubBranchId();
    }

    private String currentSubBranchId() {
        return branchId + String.format("%02d", subBranchId);
    }

}
