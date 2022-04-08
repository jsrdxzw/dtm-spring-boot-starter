package com.jsrdxzw.dtmspringbootstarter.example;

import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.client.HttpClient;
import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmServerRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.TransactionResponse;
import com.jsrdxzw.dtmspringbootstarter.core.msg.Msg;
import com.jsrdxzw.dtmspringbootstarter.core.saga.Saga;
import com.jsrdxzw.dtmspringbootstarter.core.tcc.Tcc;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author xuzhiwei
 * @date 2022/4/5 15:46
 */
@RestController
public class TransController {

    @Autowired
    private TransService transService;

    private static final String HOST = "http://localhost:9090";

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private DataSource dataSource;

    @Value("${dtm.http-server}")
    private String httpServer;

    @PostMapping("/SagaBTransOut")
    public String sagaTransOut(DtmServerRequest request) throws Exception {
        System.out.println("SagaBTransOut");
//        BranchBarrier branchBarrier = new BranchBarrier(request);
//        branchBarrier.call(dataSource.getConnection(), barrier -> {
//            transService.sagaTransOut(request);
//        });
        return "12";
    }

    @PostMapping("/SagaBTransIn")
    public String sagaTransIn(DtmServerRequest request) throws Exception {
        System.out.println("SagaBTransIn");
        return "SagaBTransIn";
    }


    @PostMapping("/SagaBTransOutCom")
    public TransactionResponse sagaTransOutCom(DtmServerRequest request) throws Exception {
        System.out.println("sagaTransOutCom");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            transService.sagaTransOutCom(request);
        });
        return TransactionResponse.builder().dtmResult(DtmResultEnum.SUCCESS.name()).build();
    }

    @PostMapping("/SagaBTransInCom")
    public String sagaTransInCom(DtmServerRequest request) throws Exception {

        return "SagaBTransInCom";
    }

    @PostMapping("/submit")
    public String submit() {
        TransReq req = new TransReq();
        req.setAmount(BigDecimal.valueOf(100));
        new Saga(httpServer, httpClient.getNewGid().getGid())
                .add(HOST + "/SagaBTransOut", HOST + "/SagaBTransOutCom", req)
                .add(HOST + "/SagaBTransIn", HOST + "/SagaBTransInCom", req)
                .submit(httpClient);
        return "ok";
    }

    @PostMapping("/tccSubmit")
    public String tccSubmit() {
        String gid = httpClient.getNewGid().getGid();
        new Tcc(httpServer, gid).tccGlobalTransaction(httpClient, tcc -> {
            tcc.callBranch(
                    httpClient, null, HOST + "/tccTransOutTry", HOST + "/tccTransOutConfirm", HOST + "/tccTransOutCancel");
            tcc.callBranch(
                    httpClient, null, HOST + "/tccTransInTry", HOST + "/tccTransInConfirm", HOST + "/tccTransInCancel");
        });
        return gid;
    }

    @PostMapping("/tccTransInTry")
    public String tccTransInTry(DtmServerRequest request) throws Exception {
        System.out.println("tccTransInTry");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransInTry call");
        });
        throw DtmException.failure();
    }

    @PostMapping("/tccTransInConfirm")
    public String tccTransInConfirm(DtmServerRequest request) throws Exception {
        System.out.println("tccTransInConfirm");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransInConfirm call");
        });
        return "tccTransInConfirm";
    }

    @PostMapping("/tccTransInCancel")
    public String tccTransInCancel(DtmServerRequest request) throws Exception {
        System.out.println("tccTransInCancel");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransInCancel call");
        });
        return "tccTransInCancel";
    }

    @PostMapping("/tccTransOutTry")
    public String tccTransOutTry(DtmServerRequest request) throws Exception {
        System.out.println("tccTransOutTry");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransOutTry call");
        });
        return "tccTransOutTry";
    }

    @PostMapping("/tccTransOutConfirm")
    public String tccTransOutConfirm(DtmServerRequest request) throws Exception {
        System.out.println("tccTransOutConfirm");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransOutConfirm call");
        });
        return "tccTransOutConfirm";
    }

    @PostMapping("/tccTransOutCancel")
    public String tccTransOutCancel(DtmServerRequest request) throws Exception {
        System.out.println("tccTransOutCancel");
        BranchBarrier branchBarrier = new BranchBarrier(request);
        branchBarrier.call(dataSource.getConnection(), barrier -> {
            System.out.println("tccTransOutCancel call");
        });
        return "tccTransOutCancel";
    }

    @GetMapping("/query")
    public String query() {
        return "ok";
    }

    @PostMapping("/httpmsg")
    public String httpMsg() {
        TransReq req = TransReq.builder().amount(BigDecimal.valueOf(200)).build();
        String gid = httpClient.getNewGid().getGid();
        Msg msg = new Msg(httpServer, gid).add(HOST + "/SagaBTransOut", req)
                .add(HOST + "/SagaBTransIn", req);
        msg.prepare(httpClient, HOST + "/query");
        msg.submit(httpClient);
        return "ok";
    }

    @PostMapping("/http_msg_doAndCommit")
    public String httpMsgDoAndCommit() throws SQLException {
        TransReq req = TransReq.builder().amount(BigDecimal.valueOf(200)).build();
        String gid = httpClient.getNewGid().getGid();
        Msg msg = new Msg(httpServer, gid).add(HOST + "/SagaBTransIn", req);
        msg.doAndSubmitDb(httpClient, dataSource.getConnection(), HOST + "/query", barrier -> {
            System.out.println("submit db");
        });
//        msg.prepare(httpClient, HOST + "/query");
//        msg.submit(httpClient);
        return "ok";
    }
}
