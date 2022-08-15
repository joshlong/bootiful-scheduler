package com.joshlong.scheduling.engine;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Josh Long
 */
@Configuration
class SchedulingAutoConfiguration {

	/**
	 * Shared by various components in the engine so they can all see the current,
	 * triggering {@link Instant}
	 */
	private final AtomicReference<Instant> currentInstant = new AtomicReference<>();

	@Bean
	TaskScheduler taskScheduler() {
		return new TaskSchedulerBuilder().poolSize(10).build();
	}

	/**
	 * The custom {@link org.springframework.scheduling.Trigger trigger } implementation
	 */
	@Bean
	ScheduleTrigger scheduleTrigger() {
		return new ScheduleTrigger(this.currentInstant);
	}

	@Bean
	DefaultSchedulingService schedulingService(ScheduleTrigger trigger, ApplicationEventPublisher publisher,
			TaskScheduler taskScheduler) {
		return new DefaultSchedulingService(this.currentInstant, trigger, publisher, taskScheduler);
	}

}
