package com.joshlong.scheduling.engine;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

@Configuration
class SchedulingConfiguration {

    @Bean
    TaskScheduler taskScheduler() {
        return new TaskSchedulerBuilder().poolSize(10).build();
    }

    @Bean
    SchedulingService schedulingService(ApplicationEventPublisher publisher, TaskScheduler taskScheduler) {
        return new SchedulingService(publisher, taskScheduler);
    }

}
