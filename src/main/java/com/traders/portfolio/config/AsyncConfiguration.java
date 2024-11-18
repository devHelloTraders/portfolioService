package com.traders.portfolio.config;

//import com.traders.common.appconfig.async.ExceptionHandlingAsyncTaskExecutor;

import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@Profile("!testdev & !testprod")
public class AsyncConfiguration extends com.traders.common.config.AsyncConfiguration {


    public AsyncConfiguration(TaskExecutionProperties taskExecutionProperties) {
        super(taskExecutionProperties);
    }
}
