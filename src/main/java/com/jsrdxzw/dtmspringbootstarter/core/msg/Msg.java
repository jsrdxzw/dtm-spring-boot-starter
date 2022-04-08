package com.jsrdxzw.dtmspringbootstarter.core.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.client.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.enums.*;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author xuzhiwei
 * @date 2022/4/7 22:06
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Msg extends TransactionBase {

    public Msg(String server, String gid) {
        super(gid, TransType.MSG.getDesc(), server, "");
    }

    public Msg add(String action, Object postData) {
        Map<String, String> step = new HashMap<>();
        step.put("action", action);
        List<Map<String, String>> steps = getSteps();
        steps.add(step);
        String payload = JsonUtil.writeToString(postData);
        List<String> payloads = getPayloads();
        payloads.add(payload);
        return this;
    }

    public void prepare(HttpClient httpClient, String queryPrepared) {
        if (StringUtils.hasText(queryPrepared)) {
            setQueryPrepared(queryPrepared);
        }
        httpClient.transCallDtm(this, TransOperation.PREPARE.getDesc());
    }

    public void submit(HttpClient httpClient) {
        httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
    }

    public void doAndSubmitDb(
            HttpClient httpClient, Connection connection, String queryPrepared, Consumer<BranchBarrier> businessCall) {
        doAndSubmit(httpClient, queryPrepared, barrier -> {
            try {
                barrier.call(connection, businessCall);
            } catch (Exception e) {
                log.error("doAndSubmitDb error: {}", e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * it is one method for the entire prepare -> business call -> submit
     * if business call return ErrFailure, then abort is called directly
     * if business call return no error other than ErrFailure, then DoAndSubmit will call queryPrepared to get the result
     *
     * @return
     */
    public void doAndSubmit(HttpClient httpClient, String queryPrepared, Consumer<BranchBarrier> businessCall) {
        BranchBarrier barrier = new BranchBarrier(getTransType(), getGid(), "00", BranchOperation.BranchMSG.getOp());
        prepare(httpClient, queryPrepared);
        try {
            businessCall.accept(barrier);
            submit(httpClient);
        } catch (Exception e) {
            if (e instanceof DtmException) {
                DtmException ex = (DtmException) e;
                if (!DtmResultEnum.FAILURE.equals(ex.getDtmResult())) {
                    try {
                        DtmRequestBranchRequest branchRequest = DtmRequestBranchRequest.buildRequestBranchRequest(
                                this, queryPrepared, barrier.getBranchId(), barrier.getOp(), HttpMethod.GET);
                        Response response = httpClient.transRequestBranch(branchRequest);
                        httpClient.catchErrorFromResponse(response);
                    } catch (DtmException dtmEx) {
                        httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
                    }
                } else {
                    httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
                }
            }
            throw e;
        }
    }
}
