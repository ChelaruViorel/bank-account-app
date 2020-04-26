package vio.account.solver.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vio.account.solver.dao.AccountDao;
import vio.account.solver.dao.AccountRequestDao;
import vio.account.solver.model.AccountRequest;
import vio.account.solver.service.AccountService;

@Slf4j
@Component
public class AccountSolverScheduler {

    @Value("${spring.application.name}")
    private String workerName;

    @Autowired
    private AccountRequestDao accountRequestDao;

    @Autowired
    private AccountService accountService;

    //@Scheduled(cron = "0 * 9-18 * * MON-FRI", zone = "Europe/Bucharest") //use this in production !!!!
    @Scheduled(cron = "*/10 * * * * *") //just for tests !!!
    public void processAccountRequests() {
        log.info("pick account request to process .... ");

        Long pickedRequestId = accountRequestDao.pickProcessableAccountRequest(workerName);
        if (pickedRequestId == null) {
            log.info("No processable requests available ! Nothing to do, exiting !");
            return;
        }

        //I picked an account request
        log.info("processing account request id=" + pickedRequestId + " ... ");

        AccountRequest request = accountRequestDao.findAccountRequestById(pickedRequestId);
        accountService.createAccount(request);

        log.info("DONE processing account request id=" + pickedRequestId + " ! ");
    }
}
