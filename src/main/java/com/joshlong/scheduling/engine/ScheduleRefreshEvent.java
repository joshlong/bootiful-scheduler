package com.joshlong.scheduling.engine;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * An event published by application code to introduce new scheduled {@link Instant
 * instants} to the schedule
 *
 * @author Josh Long
 */
public class ScheduleRefreshEvent extends ApplicationEvent {

	public ScheduleRefreshEvent(Collection<Instant> instants) {
		super(instants);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Instant> getSource() {
		return (List<Instant>) super.getSource();
	}

}
