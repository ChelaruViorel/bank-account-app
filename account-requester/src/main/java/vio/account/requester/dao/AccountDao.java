package vio.account.requester.dao;

import vio.account.requester.model.AccountRequest;
import vio.account.requester.model.AccountRequestStatus;
import vio.account.requester.model.AccountType;

public interface AccountDao {
    int countAccountsOfClient(String clientID, AccountType accountType);
    Long findLastAccountRequestId(String clientID, AccountType accountType, AccountRequestStatus[] statuses);
    long insertAccountRequest(AccountRequest request);
    String findAccountRequestStatus(long requestId);
}
