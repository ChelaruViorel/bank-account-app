package vio.account.requester.service;

import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;

import java.util.Optional;

public interface AccountService {
    boolean hasClientAccount(String clientCnp, AccountType accountType);
    Long getLastActiveAccountRequestId(String clientCnp, AccountType accountType);
    long createAccountRequest(AccountRequest request);
    Optional<AccountRequestStatus> getAccountRequestStatus(long requestId);
}
