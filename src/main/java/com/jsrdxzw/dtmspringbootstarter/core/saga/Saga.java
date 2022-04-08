package com.jsrdxzw.dtmspringbootstarter.core.saga;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.client.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.enums.TransOperation;
import com.jsrdxzw.dtmspringbootstarter.core.enums.TransType;
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

    public Saga(String server, String gid) {
        super(gid, TransType.SAGA.getDesc(), server, "");
        orders = new HashMap<>();
    }

    @SneakyThrows
    public Saga add(String action, String compensate, Object postData) {
        Map<String, String> step = new HashMap<>();
        step.put("action", action);
        step.put("compensate", compensate);
        List<Map<String, String>> steps = getSteps();
        steps.add(step);

        String payload = JsonUtil.writeToString(postData);
        List<String> payloads = getPayloads();
        payloads.add(payload);
        return this;
    }

    public Saga setConcurrent() {
        setConcurrent(true);
        return this;
    }

    public void submit(HttpClient httpClient) {
        this.buildCustomOptions();
        DtmServerResult result = httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
        if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
            String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
            throw new RuntimeException("error request saga submit : " + errMsg);
        }
    }

    private void buildCustomOptions() {
        if (Boolean.TRUE.equals(getConcurrent())) {
            Map<String, Object> map = new HashMap<>();
            map.put("orders", orders);
            map.put("concurrent", getConcurrent());
            this.setCustomData(JsonUtil.writeToString(map));
        }
    }
}
