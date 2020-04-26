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
        String sqlUpdate =
                "update account_request \n" +
                        "set status=1, processing_start_time=null, processing_end_time=null, worker_name=null \n" +
                        "where id in (\n" +
                        "    select id \n" +
                        "    from account_request \n" +
                        "    where status = 2 \n" +
                        "    and worker_name not in (\n" +
                        "              select worker_name \n" +
                        "              from account_worker_heartbeat \n" +
                        "              where heartbeat_timestamp >= now() - interval '2 minute'" +
                        "              )\n" +
                        "    )";
        int updatedRows = getJdbcTemplate().update(sqlUpdate);
        if (updatedRows > 0) {
            log.info("cleaned " + updatedRows + " account requests with dead workers !");
        }
    }

    @Override
    public Long pickProcessableAccountRequest(String workerName) {
        List<Long> processableReqIds = getJdbcTemplate().queryForList(
                "select id from account_request where status = ? order by request_timestamp asc", Long.class, NEW.getId());
        if (isEmpty(processableReqIds)) {
            return null;
        }

        for (Long reqId : processableReqIds) {
            //try to update worker name on account request for this request id and status = NEW !!!
            //because if other worker stills the request he will have changed already the status in PROCESSING
            //so this update will affect 0 rows !
            String sqlTryRegister = "update account_request set status = ?, processing_start_time = now(), worker_name = ? where id = ? and status = ?";
            getJdbcTemplate().update(sqlTryRegister, PROCESSING.getId(), workerName, reqId, NEW.getId());

            String sqlCheckRegister = "select coalesce(worker_name,'') = ? from account_request where id = ?";
            Boolean check = getJdbcTemplate().queryForObject(sqlCheckRegister, new Object[]{workerName, reqId}, Boolean.class);
            if (Boolean.TRUE.equals(check)) {
                return reqId;
            }
        }

        return null;
    }

    @Override
    public void finishProcessingRequest(long requestId) {
        String sqlUpdate = "update account_request set status = ?, processing_end_time = now() where id = ?";
        getJdbcTemplate().update(sqlUpdate, PROCESSED.getId(), requestId);
    }

    @Override
    public AccountRequest findAccountRequestById(long requestId) {
        String sql = "select * from account_request where id = ?";
        List<AccountRequest> requests = getJdbcTemplate().query(sql, new Object[]{requestId}, new AccountRequestRowMapper());
        return isEmpty(requests) ? null : requests.get(0);
    }

}