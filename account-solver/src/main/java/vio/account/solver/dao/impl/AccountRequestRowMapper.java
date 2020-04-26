package vio.account.solver.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import vio.account.solver.model.AccountRequest;
import vio.account.solver.model.AccountRequestStatus;
import vio.account.solver.model.AccountType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class AccountRequestRowMapper implements RowMapper<AccountRequest> {
    @Override
    public AccountRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountRequest req = new AccountRequest();
        req.setId(rs.getLong("id"));
        req.setClientCnp(rs.getString("client_cnp"));
        req.setAccountType(AccountType.fromId(rs.getObject("account_type", Integer.class)));
        req.setInitialDeposit(rs.getDouble("initial_deposit"));
        req.setAgentUsername(rs.getString("agent_username"));

        Timestamp requestTimestamp = rs.getTimestamp("request_timestamp");
        req.setRequestTimestamp(requestTimestamp == null ? null : requestTimestamp.toInstant());

        req.setStatus(AccountRequestStatus.fromId(rs.getObject("status", Integer.class)));

        Timestamp processingStartTime = rs.getTimestamp("processing_start_time");
        req.setProcessingStartTime(processingStartTime == null ? null : processingStartTime.toInstant());

        Timestamp processingEndTime = rs.getTimestamp("processing_end_time");
        req.setProcessingEndTime(processingEndTime == null ? null : processingEndTime.toInstant());
        req.setWorkerName(rs.getString("worker_name"));
        return req;
    }
}
