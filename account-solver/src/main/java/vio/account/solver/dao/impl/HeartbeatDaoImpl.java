package vio.account.solver.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import vio.account.solver.dao.HeartbeatDao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Repository
public class HeartbeatDaoImpl extends JdbcDaoSupport implements HeartbeatDao {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public void updateHeartbeat(String workerName) {
        getJdbcTemplate().update("insert into account_worker_heartbeat values(?, now()) ON CONFLICT (worker_name) DO update set heartbeat_timestamp =now()",
                new Object[]{workerName});
    }
}
