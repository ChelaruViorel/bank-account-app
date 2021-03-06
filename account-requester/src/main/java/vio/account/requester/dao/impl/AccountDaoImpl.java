package vio.account.requester.dao.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import vio.account.requester.dao.AccountDao;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import static java.sql.Timestamp.from;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.springframework.util.CollectionUtils.isEmpty;
import static vio.account.requester.model.AccountRequestStatus.fromId;

@Repository
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public int countAccountsOfClient(String clientID, AccountType accountType) {
        checkRequestMandatoryArguments(clientID, accountType);

        return getJdbcTemplate()
                .queryForObject(
                        "select count(*) from account where client_cnp = ? and account_type = ?"
                        , new Object[]{clientID, accountType.getId()}
                        , Integer.class);
    }

    @Override
    public Long findLastAccountRequestId(String clientID, AccountType accountType, AccountRequestStatus[] statuses) {
        checkRequestMandatoryArguments(clientID, accountType);

        var params = new LinkedList<>();
        params.add(clientID);
        params.add(accountType.getId());

        var conditionStatus = "";
        if (!ArrayUtils.isEmpty(statuses)) {
            conditionStatus = " and status IN (" + repeat("?", ",", statuses.length) + ") ";
            Arrays.stream(statuses).map(AccountRequestStatus::getId).forEach(statusId -> params.add(statusId));
        }

        var ids = getJdbcTemplate().queryForList(
                "select id from account_request where client_cnp = ? and account_type = ? " + conditionStatus + " order by request_timestamp desc limit 2"
                , Long.class, params.toArray());

        return isEmpty(ids) ? null : ids.get(0);
    }

    private void checkRequestMandatoryArguments(String clientID, AccountType accountType) {
        if (StringUtils.isEmpty(clientID)) {
            throw new IllegalArgumentException("clientID must not be empty");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("accountType must not be empty");
        }
    }

    @Override
    public long insertAccountRequest(AccountRequest request) {
        var requestId = getJdbcTemplate().queryForObject("select nextval('account_request_seq')", Long.class);
        var sqlInsert = """
                insert into account_request(id, client_cnp, account_type, initial_deposit, agent_username, request_timestamp, status) 
                values ("""
                + StringUtils.repeat("?", ",", 7) +
                """ 
                ) """;

        getJdbcTemplate().update(sqlInsert, requestId, request.getClientCnp(), request.getAccountType().getId(),
                request.getInitialDeposit(), request.getAgentUsername(), from(request.getRequestTimestamp()), request.getStatus().getId());
        return requestId;
    }

    @Override
    public Optional<AccountRequestStatus> findAccountRequestStatus(long requestId) {
        var statuses = getJdbcTemplate().queryForList("select status from account_request where id = ?", new Object[]{requestId}, Integer.class);
        return isEmpty(statuses) ? Optional.empty() : fromId(statuses.get(0));
    }
}
