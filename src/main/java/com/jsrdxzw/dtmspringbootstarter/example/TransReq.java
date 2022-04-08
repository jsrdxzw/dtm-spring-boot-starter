package com.jsrdxzw.dtmspringbootstarter.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author xuzhiwei
 * @date 2022/4/5 16:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransReq {
    private BigDecimal amount;
}
