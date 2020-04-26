package vio.account.solver.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vio.account.solver.dao.AccountRequestDao;

@Slf4j
@Component
public class AccountRequestDeadWorkerCleanupScheduler {

    @Autowired
    private AccountRequestDao accountRequestDao;

    @Scheduled(cron = "0 * * * * *")
    public void cleanupDeadWorkers(){
        log.info("cleanup account requests with dead workers so that other workers can pick the requests.");
        accountRequestDao.cleanupAccountRequestWithDeadWorkers();
    }
}
