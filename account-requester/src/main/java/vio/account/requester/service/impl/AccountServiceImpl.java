package vio.account.requester.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vio.account.requester.dao.AccountDao;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;
import vio.account.requester.service.AccountService;

import java.util.Optional;

import static vio.account.requester.model.AccountRequestStatus.*;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public boolean hasClientAccount(String clientCnp, AccountType accountType) {
        int nrSavingsAccounts = accountDao.countAccountsOfClient(clientCnp, accountType);
        return nrSavingsAccounts > 0;

    }

    @Override
    public Long getLastActiveAccountRequestId(String clientCnp, AccountType accountType) {
        return accountDao.findLastAccountRequestId(clientCnp, accountType, new AccountRequestStatus[]{NEW, PROCESSING});
    }

    @Override
    public long createAccountRequest(AccountRequest request) {
       return accountDao.insertAccountRequest(request);
    }

    @Override
    public Optional<AccountRequestStatus> getAccountRequestStatus(long requestId) {
        return accountDao.findAccountRequestStatus(requestId);
    }
}
