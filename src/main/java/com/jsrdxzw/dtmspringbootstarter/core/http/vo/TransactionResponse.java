package com.jsrdxzw.dtmspringbootstarter.core.http.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import lombok.Data;

/**
 * @author xuzhiwei
 * @date 2022/4/6 22:05
 */
@Data
public class TransactionResponse {

    @JsonProperty("dtm_result")
    private String dtmResult;

    public static TransactionResponse dtmSuccess() {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.dtmResult = DtmResultEnum.SUCCESS.name();
        return transactionResponse;
    }
}
