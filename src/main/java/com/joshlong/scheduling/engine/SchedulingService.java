package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
class SchedulingService implements Runnable {

	private final TaskScheduler taskScheduler;

	private final ApplicationEventPublisher publisher;

	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();

	SchedulingService(ApplicationEventPublisher publisher, TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
		this.publisher = publisher;
	}

	@EventListener
	public void refreshSchedule(ScheduleRefreshEvent event) {
		var list = event.getSource();
		var scheduledFuture = this.future.get();
		if (scheduledFuture != null) {
			var cancelled = scheduledFuture.cancel(true);
			Assert.isTrue(cancelled || scheduledFuture.isDone() || scheduledFuture.isCancelled(),
					"the future must at some point complete.");
		}
		var schedule = this.taskScheduler.schedule(this, new ScheduleTrigger(list));
		this.future.set(schedule);
	}

	@Override
	public void run() {
		this.publisher.publishEvent(new ScheduleEvent(new Date()));
	}

}
