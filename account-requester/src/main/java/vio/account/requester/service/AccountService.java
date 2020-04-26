package vio.account.requester.service;

import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountType;

public interface AccountService {
    boolean hasClientAccount(String clientCnp, AccountType accountType);
    Long getLastActiveAccountRequestId(String clientCnp, AccountType accountType);
    long createAccountRequest(AccountRequest request);
    String getAccountRequestStatus(long requestId);
}
