package com.jsrdxzw.dtmspringbootstarter.core.http.ro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuzhiwei
 * @date 2022/4/6 12:19
 */
@SuppressWarnings("ALL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtmServerRequest {
    private String dtm;

    private String trans_type;

    private String branch_id;

    private String gid;

    private String op;
}
