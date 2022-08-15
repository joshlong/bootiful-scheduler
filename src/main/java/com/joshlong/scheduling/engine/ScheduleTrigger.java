package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

	private final List<Date> dates;

	private final AtomicInteger offset = new AtomicInteger(0);

	private final AtomicReference<Date> dateAtomicReference;

	ScheduleTrigger(AtomicReference<Date> dateThreadLocal, List<Date> dates) {
		var now = new Date();
		this.dates = dates.stream().distinct().sorted(Date::compareTo).filter(d -> d.after(now)).toList();
		log.debug("going to trigger for the following dates [" + this.dates + "]");
		this.dateAtomicReference = dateThreadLocal;
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		var offset = this.offset.getAndIncrement();
		var returnDate = (Date) null;
		if (offset < this.dates.size())
			returnDate = this.dates.get(offset);
		this.dateAtomicReference.set(returnDate);
		debug(returnDate, offset);
		return returnDate;
	}

	private static void debug(Date returnDate, int offset) {
		if (log.isDebugEnabled()) {
			log.debug(
					Map.of("offset", offset, "returnDate", null == returnDate ? "" : returnDate.toString()).toString());
		}
	}

}
