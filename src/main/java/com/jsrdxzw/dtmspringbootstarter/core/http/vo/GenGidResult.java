package com.jsrdxzw.dtmspringbootstarter.core.http.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xuzhiwei
 * @date 2022/4/5 16:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GenGidResult extends DtmServerResult {
    private String gid;
}