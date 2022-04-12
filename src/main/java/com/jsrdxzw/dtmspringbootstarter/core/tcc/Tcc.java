package com.jsrdxzw.dtmspringbootstarter.core.tcc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.enums.*;
import com.jsrdxzw.dtmspringbootstarter.core.http.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRegisterBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

import static com.jsrdxzw.dtmspringbootstarter.core.enums.TransOperation.REGISTER_BRANCH;

/**
 * @author xuzhiwei
 * @date 2022/4/7 10:26
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
public class Tcc extends TransactionBase {

    public Tcc(HttpClient httpClient) {
        super(TransType.TCC.getDesc(), httpClient.getDtmServerUrl(), "");
        this.httpClient = httpClient;
    }

    /**
     * the step of try branches is completed usually
     *
     * @param func callback
     * @return DtmServerResult
     */
    public DtmServerResult tccGlobalTransaction(Consumer<Tcc> func) {
        this.retrieveDtmGid();
        try {
            DtmServerResult result = httpClient.transCallDtm(this, TransOperation.PREPARE.getDesc());
            if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
                String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
                log.error("error request tcc prepare : {}", errMsg);
                throw new RuntimeException("error request tcc prepare : " + errMsg);
            }
            func.accept(this);
            // 3. submit global transaction
            return httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
        } catch (Exception e) {
            httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
            throw e;
        }
    }

    /**
     * 1. register confirm and cancel branch to dtm
     * 2. request try to DM
     *
     * @param body       post body
     * @param tryUrl     try url
     * @param confirmUrl confirm url
     * @param cancelUrl  cancel url
     */
    public void callBranch(Object body, String tryUrl, String confirmUrl, String cancelUrl) {
        this.retrieveDtmGid();
        DtmRegisterBranchRequest registerBranchRequest = buildBranchRegisterRequest();
        if (Objects.nonNull(body)) {
            registerBranchRequest.setData(JsonUtil.writeToString(body));
        }
        registerBranchRequest.setConfirm(confirmUrl);
        registerBranchRequest.setCancel(cancelUrl);

        String branchId = this.branchIdGen.newSubBranchId();
        registerBranchRequest.setBranchId(branchId);

        // 1. register branch
        DtmServerResult result = httpClient.transCallDtm(registerBranchRequest, REGISTER_BRANCH.getDesc());
        if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
            String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
            throw new RuntimeException("error request tcc register branch : " + errMsg);
        }

        // 2. invoke try branch
        DtmRequestBranchRequest branchRequest = DtmRequestBranchRequest
                .buildRequestBranchRequest(this, body, tryUrl, branchId, BranchOperation.BranchTry.getOp(), HttpMethod.POST);
        ResponseEntity<DtmServerResult> response = httpClient.transRequestBranch(branchRequest);
        try {
            httpClient.catchErrorFromResponse(response);
        } catch (DtmException e) {
            throw e;
        } catch (Exception e) {
            log.error("request callBranch error, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private DtmRegisterBranchRequest buildBranchRegisterRequest() {
        DtmRegisterBranchRequest branchRegisterRequest = new DtmRegisterBranchRequest();
        branchRegisterRequest.setGid(this.gid);
        branchRegisterRequest.setTransType(this.transType);
        return branchRegisterRequest;
    }
}
