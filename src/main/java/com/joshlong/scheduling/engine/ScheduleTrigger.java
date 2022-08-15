package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This implementation triggers for each of a {@link java.util.Collection<Date>} date
 * collection.
 *
 * @author Josh Long
 */
@Slf4j
class ScheduleTrigger implements Trigger {

	private final AtomicInteger offset = new AtomicInteger(0);

	private final AtomicReference<List<Date>> datesAtomicReference = new AtomicReference<>(List.of());

	private final AtomicReference<Date> dateAtomicReference;

	private final Object monitor = new Object();

	ScheduleTrigger(AtomicReference<Date> dateThreadLocal) {
		this.dateAtomicReference = dateThreadLocal;
	}

	@EventListener
	public void refresh(ScheduleRefreshEvent event) {
		synchronized (this.monitor) {
			this.offset.set(0);
			var now = new Date();
			var dates = event.getSource().stream().distinct().sorted(Date::compareTo).filter(d -> d.after(now))
					.toList();
			this.datesAtomicReference.set(dates);
		}
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		synchronized (this.monitor) {
			var dates = this.datesAtomicReference.get();
			var offset = this.offset.getAndIncrement();
			var returnDate = (Date) null;
			if (offset < dates.size())
				returnDate = dates.get(offset);
			this.dateAtomicReference.set(returnDate);
			return returnDate;
		}
	}

}
