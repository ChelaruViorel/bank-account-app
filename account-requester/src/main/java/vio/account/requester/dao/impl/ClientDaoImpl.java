package vio.account.requester.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import vio.account.requester.dao.ClientDao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Repository
public class ClientDaoImpl extends JdbcDaoSupport implements ClientDao {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public boolean clientExists(String clientCnp) {
        Integer count = getJdbcTemplate().queryForObject("select count(cnp) from clients where cnp = ?", new Object[]{clientCnp}, Integer.class);
        return count != null && count > 0;
    }
}
