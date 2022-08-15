package com.joshlong.scheduling.engine;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
class SchedulingConfiguration {

	private final AtomicReference<Date> dateAtomicReference = new AtomicReference<>();

	@Bean
	TaskScheduler taskScheduler() {
		return new TaskSchedulerBuilder().poolSize(10).build();
	}

	@Bean
	ScheduleTrigger scheduleTrigger() {
		return new ScheduleTrigger(this.dateAtomicReference);
	}

	@Bean
	SchedulingService schedulingService(ScheduleTrigger trigger, ApplicationEventPublisher publisher,
			TaskScheduler taskScheduler) {
		return new SchedulingService(this.dateAtomicReference, trigger, publisher, taskScheduler);
	}

}
