package com.jsrdxzw.dtmspringbootstarter.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsrdxzw.dtmspringbootstarter.core.http.HttpClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * the base information of global transaction
 *
 * @author xuzhiwei
 * @date 2022/4/5 13:54
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionBase extends TransactionOptions {
    @JsonIgnore
    protected HttpClient httpClient;

    protected String gid;

    @JsonProperty("trans_type")
    protected String transType;

    @JsonIgnore
    protected String dtm;

    @JsonProperty("custom_data")
    protected String customData;

    @JsonProperty("steps")
    protected List<Map<String, String>> steps;

    @JsonProperty("payloads")
    protected List<String> payloads;

    @JsonIgnore
    protected List<List<Byte>> binPayloads;

    @JsonIgnore
    protected BranchIDGen branchIdGen;

    @JsonIgnore
    protected String op;

    @JsonProperty("query_prepared")
    protected String queryPrepared;

    @JsonProperty("protocol")
    protected String protocol;

    public TransactionBase(String transType, String dtm, String branchId) {
        this.transType = transType;
        this.dtm = dtm;
        BranchIDGen branchIdGen = new BranchIDGen();
        branchIdGen.setBranchId(branchId);
        this.branchIdGen = branchIdGen;
        this.payloads = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    protected void retrieveDtmGid() {
        if (StringUtils.hasText(this.gid)) {
            return;
        }
        this.gid = httpClient.getNewGid().getGid();
    }
}
