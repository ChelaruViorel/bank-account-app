package vio.account.solver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vio.account.solver.dao.AccountDao;
import vio.account.solver.dao.AccountRequestDao;
import vio.account.solver.model.Account;
import vio.account.solver.model.AccountRequest;
import vio.account.solver.service.AccountService;

import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRequestDao accountRequestDao;

    @Autowired
    private AccountDao accountDao;

    @Override
    public void createAccount(AccountRequest request) {
        Account account = new Account();
        account.setIban(UUID.randomUUID().toString());
        account.setAccountType(request.getAccountType());
        account.setClientCnp(request.getClientCnp());
        account.setCurrentAmount(request.getInitialDeposit());
        accountDao.createAccount(account);
        accountRequestDao.finishProcessingRequest(request.getId());
    }
}
