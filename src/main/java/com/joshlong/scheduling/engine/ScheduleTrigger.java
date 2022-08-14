package com.joshlong.scheduling.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class ScheduleTrigger implements Trigger {

	private final List<Date> dates;

	private final AtomicInteger offset = new AtomicInteger(0);

	ScheduleTrigger(List<Date> dates) {
		this.dates = dates.stream().distinct().sorted(Date::compareTo).toList();
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		var offset = this.offset.getAndIncrement();
		var returnDate = (Date) null;
		if (offset < this.dates.size())
			returnDate = this.dates.get(offset);
		log.debug(Map.of("offset", offset, "returnDate", null == returnDate ? "" : returnDate.toInstant().toString())
				.toString());
		return returnDate;
	}

}
