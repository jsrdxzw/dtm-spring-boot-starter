package com.jsrdxzw.dtmspringbootstarter.core.http.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuzhiwei
 * @date 2022/4/6 22:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    @JsonProperty("dtm_result")
    private String dtmResult;
}
