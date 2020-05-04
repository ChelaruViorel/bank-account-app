package vio.account.requester.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vio.account.requester.dao.AccountDao;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;
import vio.account.requester.service.AccountService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static vio.account.requester.model.AccountRequestStatus.NEW;
import static vio.account.requester.model.AccountRequestStatus.PROCESSING;

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

    @Async
    @Override
    public CompletableFuture<Optional<AccountRequestStatus>> getAccountRequestStatus(long requestId) {
        return CompletableFuture.completedFuture(accountDao.findAccountRequestStatus(requestId));
    }
}
