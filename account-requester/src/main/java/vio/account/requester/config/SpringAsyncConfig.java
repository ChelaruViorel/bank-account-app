package vio.account.requester.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class SpringAsyncConfig implements AsyncConfigurer {

    @Bean
    public Executor springAsyncTasksExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();

        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores / 2);
        executor.setMaxPoolSize(cores);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("SpringAsyncThread-");
        executor.initialize();

        log.info("Started Spring async task executor with max " + cores + " threads.");
        return executor;
    }
}
