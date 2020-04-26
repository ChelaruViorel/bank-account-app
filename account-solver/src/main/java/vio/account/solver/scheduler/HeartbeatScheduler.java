package vio.account.solver.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vio.account.solver.dao.HeartbeatDao;

@Slf4j
@Component
public class HeartbeatScheduler {

    @Value("${spring.application.name}")
    private String workerName;

    @Autowired
    private HeartbeatDao heartbeatDao;

    @Scheduled(cron = "*/5 * * * * *")
    public void updateHeartbeat() {
        log.debug("worker " + workerName + " updating heatbeat ");
        heartbeatDao.updateHeartbeat(workerName);
    }
}
