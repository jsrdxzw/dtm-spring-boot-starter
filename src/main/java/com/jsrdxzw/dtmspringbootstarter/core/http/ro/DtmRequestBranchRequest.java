package com.jsrdxzw.dtmspringbootstarter.core.http.ro;

import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/7 18:51
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DtmRequestBranchRequest {
    /**
     * get or post
     */
    private HttpMethod method;
    private String dtm;
    private String gid;
    private String op;
    private String branchId;
    private String transType;
    private String url;
    private Map<String, String> branchHeaders;
    private Object body;

    public static DtmRequestBranchRequest buildRequestBranchRequest(
            TransactionBase base, Object body, String tryUrl, String branchId, String op, HttpMethod httpMethod) {
        return DtmRequestBranchRequest.builder()
                .method(httpMethod)
                .url(tryUrl)
                .branchHeaders(base.getBranchHeaders())
                .branchId(branchId)
                .dtm(base.getDtm())
                .gid(base.getGid())
                .op(op)
                .transType(base.getTransType())
                .body(body)
                .build();
    }
}
