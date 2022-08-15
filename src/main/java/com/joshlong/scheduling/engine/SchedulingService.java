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

	void schedule(Instant instant);

	void schedule(Collection<Instant> instants);

}
