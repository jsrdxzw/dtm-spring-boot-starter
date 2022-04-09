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
    protected Boolean waitResult;

    @JsonProperty("timeout_to_fail")
    protected Long timeoutToFail;

    @JsonProperty("requestTimeout")
    protected Long requestTimeout;

    @JsonProperty("retry_interval")
    protected Long retryInterval;

    @JsonProperty("passthrough_headers")
    protected List<String> passthroughHeaders;

    @JsonProperty("branch_headers")
    protected Map<String, String> branchHeaders;

    protected Boolean concurrent;
}
