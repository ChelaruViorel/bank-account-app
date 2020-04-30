package vio.account.solver.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import vio.account.solver.dao.AccountRequestDao;
import vio.account.solver.dao.HeartbeatDao;
import vio.account.solver.model.AccountRequest;
import vio.account.solver.model.AccountRequestStatus;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.springframework.util.CollectionUtils.isEmpty;
import static vio.account.solver.model.AccountRequestStatus.*;

@Slf4j
@Repository
public class AccountRequestDaoImpl extends JdbcDaoSupport implements AccountRequestDao {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public void cleanupAccountRequestWithDeadWorkers() {
        var sqlUpdate =
                """
                           update account_request
                           set status=1, processing_start_time=null, processing_end_time=null, worker_name=null
                           where id in (
                               select id
                               from account_request
                               where status = 2
                               and worker_name not in (
                                         select worker_name
                                         from account_worker_heartbeat
                                         where heartbeat_timestamp >= now() - interval '2 minute'
                                         )
                               )
                        """;
        var updatedRows = getJdbcTemplate().update(sqlUpdate);
        if (updatedRows > 0) {
            log.info("cleaned " + updatedRows + " account requests with dead workers !");
        }
    }

    @Override
    public Optional<Long> pickProcessableAccountRequest(String workerName) {
        var processableReqIds = getJdbcTemplate().queryForList("select id from account_request where status = ? order by request_timestamp asc", Long.class, NEW.getId());
        if (isEmpty(processableReqIds)) {
            return Optional.empty();
        }

        return processableReqIds.stream()
                .peek(reqId -> {
                    //try to update worker name on account request for this request id and status = NEW !!!
                    //because if other worker stills the request he will have changed already the status in PROCESSING
                    //so this update will affect 0 rows !
                    String sqlTryRegister = "update account_request set status = ?, processing_start_time = now(), worker_name = ? where id = ? and status = ?";
                    getJdbcTemplate().update(sqlTryRegister, PROCESSING.getId(), workerName, reqId, NEW.getId());
                })
                .filter(reqId -> {
                    String sqlCheckRegister = "select coalesce(worker_name,'') = ? from account_request where id = ?";
                    Boolean check = getJdbcTemplate().queryForObject(sqlCheckRegister, new Object[]{workerName, reqId}, Boolean.class);
                    return TRUE.equals(check);
                })
                .findFirst();
    }

    @Override
    public void finishProcessingRequest(long requestId) {
        var sqlUpdate = "update account_request set status = ?, processing_end_time = now() where id = ?";
        getJdbcTemplate().update(sqlUpdate, PROCESSED.getId(), requestId);
    }

    @Override
    public Optional<AccountRequest> findAccountRequestById(long requestId) {
        var sql = "select * from account_request where id = ?";
        var requests = getJdbcTemplate().query(sql, new Object[]{requestId}, new AccountRequestRowMapper());
        return isEmpty(requests) ? Optional.empty() : Optional.of(requests.get(0));
    }

}