package com.joshlong.scheduling.engine;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Installs the relevant infrastructure for our
 * {@link org.springframework.scheduling.Trigger}-powered scheduling engine.
 *
 * This is Spring Boot autoconfiguration - there's nothing for the consumer to use,
 * really.
 *
 * @author Josh Long
 */
@Configuration
class SchedulingAutoConfiguration {

	private final AtomicReference<Instant> dateAtomicReference = new AtomicReference<>();

	@Bean
	TaskScheduler taskScheduler() {
		return new TaskSchedulerBuilder().poolSize(10).build();
	}

	@Bean
	ScheduleTrigger scheduleTrigger() {
		return new ScheduleTrigger(this.dateAtomicReference);
	}

	@Bean
	DefaultSchedulingService schedulingService(ScheduleTrigger trigger, ApplicationEventPublisher publisher,
			TaskScheduler taskScheduler) {
		return new DefaultSchedulingService(this.dateAtomicReference, trigger, publisher, taskScheduler);
	}

}
