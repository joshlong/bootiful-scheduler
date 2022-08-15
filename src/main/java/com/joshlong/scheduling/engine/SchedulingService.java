package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
class SchedulingService implements Runnable {

	private final ScheduleTrigger trigger;

	private final TaskScheduler taskScheduler;

	private final ApplicationEventPublisher publisher;

	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();

	private final AtomicReference<Date> publicationTime;

	SchedulingService(AtomicReference<Date> publicationTime, ScheduleTrigger trigger,
			ApplicationEventPublisher publisher, TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
		this.publicationTime = publicationTime;
		this.trigger = trigger;
		this.publisher = publisher;
	}

	private static boolean isFinished(ScheduledFuture<?> scheduledFuture) {
		if (null == scheduledFuture)
			return true;
		return scheduledFuture.isCancelled() || scheduledFuture.isDone();
	}

	private final Object monitor = new Object();

	@EventListener(ScheduleRefreshEvent.class)
	public void refreshSchedule() {
		synchronized (this.monitor) {
			var scheduledFuture = this.future.get();
			if (scheduledFuture == null || isFinished(scheduledFuture)) {
				log.debug("no schedule thread");
				var schedule = this.taskScheduler.schedule(this, this.trigger);
				this.future.set(schedule);
			} //
			else {
				log.debug("already have a schedule thread");
			}
		}
	}

	@Override
	public void run() {
		var date = this.publicationTime.get();
		this.publisher.publishEvent(new ScheduleEvent(date));
	}

}
