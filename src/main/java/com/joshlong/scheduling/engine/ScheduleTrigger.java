package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This implementation of Spring Framework's {@link Trigger trigger} activates
 * ("triggers") for each instant in the {@link java.util.Collection<Instant>} instants
 * collection.
 *
 * @author Josh Long
 */
@Slf4j
class ScheduleTrigger implements Trigger {

	private final AtomicInteger offset = new AtomicInteger(0);

	private final AtomicReference<List<Instant>> datesAtomicReference = new AtomicReference<>(List.of());

	private final AtomicReference<Instant> dateAtomicReference;

	private final Object monitor = new Object();

	ScheduleTrigger(AtomicReference<Instant> dateThreadLocal) {
		this.dateAtomicReference = dateThreadLocal;
	}

	@EventListener
	public void refresh(ScheduleRefreshEvent event) {
		synchronized (this.monitor) {
			this.offset.set(0);
			var now = Instant.now();
			var dates = event.getSource().stream().distinct().sorted(Instant::compareTo).filter(i -> i.isAfter(now))
					.toList();
			this.datesAtomicReference.set(dates);
		}
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		synchronized (this.monitor) {
			var dates = this.datesAtomicReference.get();
			var offset = this.offset.getAndIncrement();
			var returnDate = (Instant) null;
			if (offset < dates.size()) {
				returnDate = dates.get(offset);
			}
			this.dateAtomicReference.set(returnDate);
			if (null != returnDate)
				return Date.from(returnDate);
			return null;
		}
	}

}
