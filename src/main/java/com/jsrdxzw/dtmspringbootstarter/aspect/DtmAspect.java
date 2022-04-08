package com.jsrdxzw.dtmspringbootstarter.aspect;

import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmServerRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

/**
 * @author xuzhiwei
 * @date 2022/4/6 16:53
 */
@Component
@Aspect
public class DtmAspect {
    @Autowired
    private DataSource dataSource;

    @Pointcut("@annotation(com.jsrdxzw.dtmspringbootstarter.annotations.DtmBarrier)")
    private void dtmBarrier() {
    }

    @Around("dtmBarrier()")
    public Object barrier(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        DtmServerRequest request = checkBarrierArgs(args);
        BranchBarrier branchBarrier = new BranchBarrier(request);
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        Object result;
        try {
            boolean canCall = branchBarrier.call(connection);
            if (!canCall) {
                return null;
            }
            result = pjp.proceed();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
        return result;
    }

    private DtmServerRequest checkBarrierArgs(Object[] args) {
        return (DtmServerRequest) Arrays.stream(args)
                .filter(it -> it instanceof DtmServerRequest)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("not found DtmServerRequest in method params"));
    }
}