package vio.account.solver.dao;

import vio.account.solver.model.AccountRequest;

public interface AccountRequestDao {
    void cleanupAccountRequestWithDeadWorkers();
    Long pickProcessableAccountRequest(String workerName);
    void finishProcessingRequest(long requestId);
    AccountRequest findAccountRequestById(long requestId);
}
