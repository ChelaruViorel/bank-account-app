package vio.account.solver.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import vio.account.solver.dao.AccountDao;
import vio.account.solver.dao.HeartbeatDao;
import vio.account.solver.model.Account;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Repository
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public void createAccount(Account account) {
        String sqlInsert = "insert into account (iban, account_type, client_cnp, current_amount) values (?,?,?,?)";
        getJdbcTemplate().update(sqlInsert, account.getIban(), account.getAccountType().getId(), account.getClientCnp(), account.getCurrentAmount());
    }
}
