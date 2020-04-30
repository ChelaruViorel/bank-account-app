package vio.account.solver.dao;

import vio.account.solver.model.AccountRequest;

import java.util.Optional;

public interface AccountRequestDao {
    void cleanupAccountRequestWithDeadWorkers();
    Optional<Long> pickProcessableAccountRequest(String workerName);
    void finishProcessingRequest(long requestId);
    Optional<AccountRequest> findAccountRequestById(long requestId);
}
