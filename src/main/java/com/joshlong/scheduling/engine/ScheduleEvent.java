package com.joshlong.scheduling.engine;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * An event published when a scheduled {@link Instant} has arrived
 *
 * @author Josh Long
 */
public class ScheduleEvent extends ApplicationEvent {

	public ScheduleEvent(Instant source) {
		super(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Instant getSource() {
		return (Instant) super.getSource();
	}

}
