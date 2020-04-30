package vio.account.solver.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import vio.account.solver.model.AccountRequest;
import vio.account.solver.model.AccountRequestStatus;
import vio.account.solver.model.AccountType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;


public class AccountRequestRowMapper implements RowMapper<AccountRequest> {
    @Override
    public AccountRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountRequest req = new AccountRequest();
        req.setId(rs.getLong("id"));
        req.setClientCnp(rs.getString("client_cnp"));

        Optional<AccountType> accountTypeOpt = AccountType.fromId(rs.getObject("account_type", Integer.class));
        if (!accountTypeOpt.isEmpty()) {
            req.setAccountType(accountTypeOpt.get());
        }

        req.setInitialDeposit(rs.getDouble("initial_deposit"));
        req.setAgentUsername(rs.getString("agent_username"));

        Timestamp requestTimestamp = rs.getTimestamp("request_timestamp");
        req.setRequestTimestamp(requestTimestamp == null ? null : requestTimestamp.toInstant());

        Optional<AccountRequestStatus> statusOpt = AccountRequestStatus.fromId(rs.getObject("status", Integer.class));
        if (statusOpt.isEmpty()) {
            req.setStatus(statusOpt.get());
        }

        Timestamp processingStartTime = rs.getTimestamp("processing_start_time");
        req.setProcessingStartTime(processingStartTime == null ? null : processingStartTime.toInstant());

        Timestamp processingEndTime = rs.getTimestamp("processing_end_time");
        req.setProcessingEndTime(processingEndTime == null ? null : processingEndTime.toInstant());
        req.setWorkerName(rs.getString("worker_name"));
        return req;
    }
}
