package com.jsrdxzw.dtmspringbootstarter.core.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.enums.*;
import com.jsrdxzw.dtmspringbootstarter.core.http.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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

    public Msg(HttpClient httpClient) {
        super(TransType.MSG.getDesc(), httpClient.getDtmServerUrl(), "");
        this.httpClient = httpClient;
    }

    public Msg add(String action, Object postData) {
        Map<String, String> step = new HashMap<>();
        step.put("action", action);
        this.steps.add(step);

        String payload = JsonUtil.writeToString(postData);
        this.payloads.add(payload);

        return this;
    }

    public void prepare(String queryPrepared) {
        this.retrieveDtmGid();
        if (StringUtils.hasText(queryPrepared)) {
            this.queryPrepared = queryPrepared;
        }
        httpClient.transCallDtm(this, TransOperation.PREPARE.getDesc());
    }

    public void submit() {
        this.retrieveDtmGid();
        httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
    }

    public void doAndSubmitDb(
            DataSourceTransactionManager transactionManager, String queryPrepared, Consumer<BranchBarrier> businessCall) {
        doAndSubmit(queryPrepared, barrier -> {
            barrier.call(transactionManager, businessCall);
        });
    }

    /**
     * it is one method for processing:
     * <p>
     * 1. prepare
     * 2. business call
     * 3. submit
     * </p>
     * if business call return ErrFailure, then abort is called directly.
     * if business call return no error rather than ErrFailure, then DoAndSubmit will call queryPrepared to get the result
     *
     * @param queryPrepared query url
     * @param businessCall  business logic call function
     */
    public void doAndSubmit(String queryPrepared, Consumer<BranchBarrier> businessCall) {
        this.retrieveDtmGid();
        BranchBarrier barrier = new BranchBarrier(getTransType(), getGid(), "00", BranchOperation.BranchMSG.getOp());
        prepare(queryPrepared);
        try {
            businessCall.accept(barrier);
            submit();
        } catch (Exception e) {
            if (e instanceof DtmException) {
                DtmException ex = (DtmException) e;
                if (!DtmResultEnum.FAILURE.equals(ex.getDtmResult())) {
                    try {
                        DtmRequestBranchRequest branchRequest = DtmRequestBranchRequest.buildRequestBranchRequest(
                                this, null, queryPrepared, barrier.getBranchId(), barrier.getOp(), HttpMethod.GET);
                        ResponseEntity<DtmServerResult> response = httpClient.transRequestBranch(branchRequest);
                        httpClient.catchErrorFromResponse(response);
                    } catch (DtmException dtmEx) {
                        httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
                    } catch (Exception ox) {
                        log.error("dtm request error in msg: {}", ox.getMessage());
                    }
                } else {
                    httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
                }
            }
            throw e;
        }
    }
}
