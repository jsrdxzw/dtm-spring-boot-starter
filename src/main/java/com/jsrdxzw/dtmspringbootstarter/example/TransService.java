package com.jsrdxzw.dtmspringbootstarter.example;

import com.jsrdxzw.dtmspringbootstarter.annotations.DtmBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmServerRequest;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author xuzhiwei
 * @date 2022/4/6 17:47
 */
@Service
public class TransService {

    @Autowired
    private DataSource dataSource;

//    @DtmBarrier
    public String sagaTransOutCom(DtmServerRequest request) {
        return "sagaTransOutCom";
    }

//    @DtmBarrier
    public String sagaTransOut(DtmServerRequest request) {
        System.out.println("sagaTransOut");
//        BranchBarrier dtmBarrier = new BranchBarrier(request);
//        dtmBarrier.call(dataSource.getConnection(), barrier -> {
//            System.out.println("sagaTransOut");
//            throw DtmException.failure();
//        });
        throw DtmException.failure();
//        return "123";
    }

//    @DtmBarrier
    public void sagaTransIn(DtmServerRequest request) {
        System.out.println("sagaTransIn");
    }

//    @DtmBarrier
    public String sagaTransInCom(DtmServerRequest request) {
        System.out.println("sagaTransInCom");
        return "sagaTransInCom";
    }
}
