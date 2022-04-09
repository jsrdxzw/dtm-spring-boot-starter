package com.jsrdxzw.dtmspringbootstarter.core.http.ro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuzhiwei
 * @date 2022/4/6 12:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DtmServerRequest {
    private String dtm;

    private String transType;

    private String branchId;

    private String gid;

    private String op;
}
