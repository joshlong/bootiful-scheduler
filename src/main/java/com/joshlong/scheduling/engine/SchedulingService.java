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

    private final AtomicReference<Date> publicationTime = new AtomicReference<>();

    SchedulingService(ApplicationEventPublisher publisher, TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.publisher = publisher;
    }

    private static boolean isFinished(ScheduledFuture<?> scheduledFuture) {
        return scheduledFuture.isCancelled() || scheduledFuture.isDone();
    }

    @EventListener
    public void refreshSchedule(ScheduleRefreshEvent event) {
        var list = event.getSource();
        var scheduledFuture = this.future.get();
        if (scheduledFuture != null) {
            if (!isFinished(scheduledFuture)) {
                var completed = scheduledFuture.cancel(true) || isFinished(scheduledFuture);
                Assert.isTrue(completed, "the " + ScheduledFuture.class.getName() + " must at some point complete.");
                log.debug("we managed to cancel the " + ScheduledFuture.class.getName());
            }
        }
        var scheduleTrigger = new ScheduleTrigger(this.publicationTime, list);
        var schedule = this.taskScheduler.schedule(this, scheduleTrigger);
        this.future.set(schedule);
    }

    @Override
    public void run() {
        var date = this.publicationTime.get();
        this.publisher.publishEvent(new ScheduleEvent(date));
    }

}
