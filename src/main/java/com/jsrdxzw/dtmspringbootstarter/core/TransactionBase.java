package com.jsrdxzw.dtmspringbootstarter.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private String gid;

    @JsonProperty("trans_type")
    private String transType;

    @JsonIgnore
    private String dtm;

    @JsonProperty("custom_data")
    private String customData;

    @JsonProperty("steps")
    private List<Map<String, String>> steps;

    @JsonProperty("payloads")
    private List<String> payloads;

    @JsonIgnore
    private List<List<Byte>> binPayloads;

    @JsonIgnore
    private BranchIDGen branchIdGen;

    @JsonIgnore
    private String op;

    @JsonProperty("query_prepared")
    private String queryPrepared;

    @JsonProperty("protocol")
    private String protocol;

    public TransactionBase(String gid, String transType, String dtm, String branchId) {
        this.gid = gid;
        this.transType = transType;
        this.dtm = dtm;
        BranchIDGen branchIdGen = new BranchIDGen();
        branchIdGen.setBranchId(branchId);
        this.branchIdGen = branchIdGen;
        this.payloads = new ArrayList<>();
        this.steps = new ArrayList<>();
    }
}
