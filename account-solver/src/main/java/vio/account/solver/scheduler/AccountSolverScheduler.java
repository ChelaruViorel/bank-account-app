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

import java.util.Optional;

@Slf4j
@Component
public class AccountSolverScheduler {

    @Value("${spring.application.name}")
    private String workerName;

    @Autowired
    private AccountRequestDao accountRequestDao;

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "${scheduler.account.solver.crontab}", zone = "Europe/Bucharest")
    public void processAccountRequests() {
        log.info("pick account request to process .... ");

        Optional<Long> pickedRequestId = accountRequestDao.pickProcessableAccountRequest(workerName);
        if (pickedRequestId.isEmpty()) {
            log.info("No processable requests available ! Nothing to do, exiting !");
            return;
        }

        //I picked an account request
        log.info("processing account request id=" + pickedRequestId + " ... ");

        Optional<AccountRequest> request = accountRequestDao.findAccountRequestById(pickedRequestId.get());
        if(request.isEmpty()) {
            log.warn("Picked an account request that does not exist any more !!! request id="+pickedRequestId);
            return;
        }
        accountService.createAccount(request.get());

        log.info("DONE processing account request id=" + pickedRequestId + " ! ");
    }
}
