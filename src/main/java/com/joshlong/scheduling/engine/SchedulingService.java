package com.joshlong.scheduling.engine;

import java.time.Instant;
import java.util.Collection;

/**
 * Clients may use this interface if they wish to interact with the scheduler directly,
 * though this is discouraged in deference to publishing {@link ScheduleRefreshEvent
 * events} with Spring's {@link org.springframework.context.ApplicationEventPublisher
 * event publication mechanism}.
 *
 * @author Josh Long
 */
public interface SchedulingService {

	/**
	 * Shortcut that simply invokes {@link #schedule(Collection) } with a collection of
	 * one-value
	 * @param instant the {@link Instant} to trigger on
	 */
	void schedule(Instant instant);

	/**
	 * Adds all the {@link Instant} instances to the schedule. This method eliminates any
	 * duplicate instances, or instances in the past as of the time of this method's
	 * invocation
	 * @param instants a {@link Collection collection} of {@link Instant}
	 */
	void schedule(Collection<Instant> instants);

}
