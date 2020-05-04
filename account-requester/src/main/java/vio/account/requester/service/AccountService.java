package vio.account.requester.service;

import org.springframework.scheduling.annotation.Async;
import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AccountService {
    boolean hasClientAccount(String clientCnp, AccountType accountType);
    Long getLastActiveAccountRequestId(String clientCnp, AccountType accountType);
    long createAccountRequest(AccountRequest request);
    CompletableFuture<Optional<AccountRequestStatus>> getAccountRequestStatus(long requestId);
}
