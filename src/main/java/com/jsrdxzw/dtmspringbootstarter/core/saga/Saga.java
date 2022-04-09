package com.jsrdxzw.dtmspringbootstarter.core.saga;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.enums.TransOperation;
import com.jsrdxzw.dtmspringbootstarter.core.enums.TransType;
import com.jsrdxzw.dtmspringbootstarter.core.http.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/5 13:53
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
public class Saga extends TransactionBase {

    private Map<Integer, List<Integer>> orders;

    public Saga(HttpClient httpClient) {
        super(TransType.SAGA.getDesc(), httpClient.getDtmServerUrl(), "");
        this.orders = new HashMap<>();
        this.httpClient = httpClient;
    }

    public Saga addBranchOrder(int branch, List<Integer> preBranches) {
        this.orders.put(branch, preBranches);
        return this;
    }

    public Saga waitResult() {
        this.waitResult = true;
        return this;
    }

    public Saga enableConcurrent() {
        this.concurrent = true;
        return this;
    }

    @SneakyThrows
    public Saga add(String action, String compensate, Object postData) {
        Map<String, String> step = new HashMap<>();
        step.put("action", action);
        step.put("compensate", compensate);
        this.steps.add(step);

        String payload = JsonUtil.writeToString(postData);
        this.payloads.add(payload);

        return this;
    }

    public DtmServerResult submit() {
        this.retrieveDtmGid();
        this.buildCustomOptions();
        DtmServerResult result = httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
        if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
            String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
            throw new RuntimeException("error request saga submit : " + errMsg);
        }
        return result;
    }

    private void buildCustomOptions() {
        if (Boolean.TRUE.equals(this.concurrent)) {
            Map<String, Object> map = new HashMap<>();
            map.put("orders", this.orders);
            map.put("concurrent", this.concurrent);
            this.customData = JsonUtil.writeToString(map);
        }
    }
}
