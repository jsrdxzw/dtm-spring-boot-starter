package com.jsrdxzw.dtmspringbootstarter.core.http.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import lombok.Data;

/**
 * @author xuzhiwei
 * @date 2022/4/5 20:08
 */
@Data
public class DtmServerResult {
    private String message;

    @JsonProperty("dtm_result")
    private DtmResultEnum result;
}
