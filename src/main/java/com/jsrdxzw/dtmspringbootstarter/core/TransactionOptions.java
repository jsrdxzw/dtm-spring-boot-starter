package com.jsrdxzw.dtmspringbootstarter.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/5 13:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TransactionOptions {

    @JsonProperty("wait_result")
    private Boolean waitResult;

    @JsonProperty("timeout_to_fail")
    private Long timeoutToFail;

    @JsonProperty("requestTimeout")
    private Long requestTimeout;

    @JsonProperty("retry_interval")
    private Long retryInterval;

    @JsonProperty("passthrough_headers")
    private List<String> passthroughHeaders;

    @JsonProperty("branch_headers")
    private Map<String, String> branchHeaders;

    private Boolean concurrent;
}
