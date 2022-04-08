package com.jsrdxzw.dtmspringbootstarter.core.tcc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsrdxzw.dtmspringbootstarter.core.BranchIDGen;
import com.jsrdxzw.dtmspringbootstarter.core.TransactionBase;
import com.jsrdxzw.dtmspringbootstarter.core.client.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.enums.*;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRegisterBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
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

    public Tcc(String server, String gid) {
        super(gid, TransType.TCC.getDesc(), server, "");
    }

    public void tccGlobalTransaction(HttpClient httpClient, Consumer<Tcc> func) {
        try {
            DtmServerResult result = httpClient.transCallDtm(this, TransOperation.PREPARE.getDesc());
            if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
                String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
                log.error("error request tcc prepare : {}", errMsg);
                throw new RuntimeException("error request tcc prepare : " + errMsg);
            }
            func.accept(this);
            httpClient.transCallDtm(this, TransOperation.SUBMIT.getDesc());
        } catch (Exception e) {
            httpClient.transCallDtm(this, TransOperation.ABORT.getDesc());
            throw e;
        }
    }

    /**
     * 1. register confirm and cancel branch to dtm
     * 2. request try to DM
     *
     * @param httpClient
     * @param body
     * @param tryUrl
     * @param confirmUrl
     * @param cancelUrl
     */
    public void callBranch(HttpClient httpClient, Object body, String tryUrl, String confirmUrl, String cancelUrl) {
        DtmRegisterBranchRequest registerBranchRequest = buildBranchRegisterRequest();
        if (Objects.nonNull(body)) {
            registerBranchRequest.setData(JsonUtil.writeToString(body));
        }
        registerBranchRequest.setConfirm(confirmUrl);
        registerBranchRequest.setCancel(cancelUrl);
        BranchIDGen branchIdGen = getBranchIdGen();
        String branchId = branchIdGen.newSubBranchId();
        registerBranchRequest.setBranchId(branchId);

        DtmServerResult result = httpClient.transCallDtm(registerBranchRequest, REGISTER_BRANCH.getDesc());
        if (result.getResult() == null || !DtmResultEnum.SUCCESS.equals(result.getResult())) {
            String errMsg = StringUtils.hasText(result.getMessage()) ? result.getMessage() : "inner server error";
            throw new RuntimeException("error request tcc register branch : " + errMsg);
        }

        DtmRequestBranchRequest branchRequest = DtmRequestBranchRequest
                .buildRequestBranchRequest(this, tryUrl, branchId, BranchOperation.BranchTry.getOp(), HttpMethod.POST);
        // request try branch immediately
        Response response = httpClient.transRequestBranch(branchRequest);
        httpClient.catchErrorFromResponse(response);
    }

    private DtmRegisterBranchRequest buildBranchRegisterRequest() {
        DtmRegisterBranchRequest branchRegisterRequest = new DtmRegisterBranchRequest();
        branchRegisterRequest.setGid(getGid());
        branchRegisterRequest.setTransType(getTransType());
        return branchRegisterRequest;
    }
}
