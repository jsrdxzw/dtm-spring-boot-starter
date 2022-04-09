package com.jsrdxzw.dtmspringbootstarter.core.barrier;

import com.jsrdxzw.dtmspringbootstarter.core.enums.BranchOperation;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmServerRequest;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.function.Consumer;

import static com.jsrdxzw.dtmspringbootstarter.core.enums.BranchOperation.BranchCancel;
import static com.jsrdxzw.dtmspringbootstarter.core.enums.BranchOperation.BranchCompensate;

/**
 * @author xuzhiwei
 * @date 2022/4/6 11:57
 */
@Slf4j
@Data
public class BranchBarrier {
    /**
     * type of global transaction
     * such as saga, tcc, xa
     */
    private String transType;

    /**
     * global unique sequence number of transaction
     */
    private String gid;

    /**
     * branch id is shown by pair such as 01, 02
     */
    private String branchId;

    /**
     * operation, such as action, compensate, try
     */
    private String op;

    private Integer barrierId;

    private String reason;

    public BranchBarrier(DtmServerRequest request) {
        this(request.getTransType(), request.getGid(), request.getBranchId(), request.getOp());
    }

    public BranchBarrier(String transType, String gid, String branchId, String op) {
        this.transType = transType;
        this.gid = gid;
        this.branchId = branchId;
        this.op = op;
        this.barrierId = 0;
    }

    /**
     * transaction barrier，to see details in https://zhuanlan.zhihu.com/p/388444465
     */
    public void call(DataSourceTransactionManager transactionManager, Consumer<BranchBarrier> bizCall) throws Exception {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        if (Objects.isNull(transactionManager.getDataSource())) {
            log.error("transaction manager datasource can not be null");
            throw new RuntimeException("transaction manager datasource can not be null");
        }

        Connection connection = DataSourceUtils.getConnection(transactionManager.getDataSource());
        this.barrierId++;
        BranchOperation currentOp = BranchOperation.fromOp(op);
        BranchOperation originOp = BranchOperation.getOriginOp(op);
        try {
            int originAffected = 0;
            if (originOp != null) {
                originAffected = insertBarrier(connection, transType, gid, branchId, originOp.getOp(), barrierId, reason);
            }
            int currentAffected = insertBarrier(connection, transType, gid, branchId, op, barrierId, reason);
            if (barrierNotThrough(currentOp, originAffected, currentAffected)) {
                return;
            }
            bizCall.accept(this);
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    public boolean call(Connection connection) {
        this.barrierId++;
        BranchOperation currentOp = BranchOperation.fromOp(op);
        BranchOperation originOp = BranchOperation.getOriginOp(op);
        int originAffected = 0;
        if (originOp != null) {
            originAffected = insertBarrier(connection, transType, gid, branchId, originOp.getOp(), barrierId, reason);
        }
        int currentAffected = insertBarrier(connection, transType, gid, branchId, op, barrierId, reason);
        // through barrier
        return !barrierNotThrough(currentOp, originAffected, currentAffected);
    }

    @SneakyThrows
    private int insertBarrier(
            Connection connection,
            String transType,
            String gid,
            String branchId,
            String op,
            int barrierId,
            String reason
    ) {
        String sql = "insert ignore into barrier (trans_type, gid, branch_id, op, barrier_id, reason) values(?,?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transType);
            preparedStatement.setString(2, gid);
            preparedStatement.setString(3, branchId);
            preparedStatement.setString(4, op);
            preparedStatement.setString(5, String.format("%02d", barrierId));
            preparedStatement.setString(6, reason);
            return preparedStatement.executeUpdate();
        }
    }

    private boolean barrierNotThrough(BranchOperation currentOp, int originAffected, int currentAffected) {
        return (currentOp == BranchCancel || currentOp == BranchCompensate) && originAffected > 0 || currentAffected == 0;
    }
}
