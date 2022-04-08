package com.jsrdxzw.dtmspringbootstarter.core.http.ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuzhiwei
 * @date 2022/4/7 17:11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtmRegisterBranchRequest {

    private String data;

    @JsonProperty("branch_id")
    private String branchId;

    private String confirm;

    private String cancel;

    private String gid;

    @JsonProperty("trans_type")
    private String transType;
}
