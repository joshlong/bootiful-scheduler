package com.joshlong.scheduling.engine;

import com.joshlong.scheduling.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class SchedulingServiceTest {

	private final ApplicationEventPublisher publisher;

	private final TriggerTest test;

	SchedulingServiceTest(@Autowired TriggerTest test, @Autowired ApplicationEventPublisher publisher) {
		this.publisher = publisher;
		this.test = test;
	}

	private void emit(Date d) {
		this.publisher.publishEvent(new ScheduleRefreshEvent(List.of(d)));
	}

	@SneakyThrows
	private void wait(int seconds) {
		Thread.sleep(seconds * 1000);
	}

	@Test
	void schedule() throws Exception {

		var now = new Date();

		var next = DateUtils.secondsLater(now, 5);
		emit(next);
		wait(6);
		Assertions.assertEquals(this.test.count.get(), 1);

		emit(DateUtils.secondsLater(now, 8));
		emit(DateUtils.secondsLater(now, 9));
		wait(10);
		Assertions.assertEquals(this.test.count.get(), 3);

	}

}

@Slf4j
@Component
class TriggerTest {

	final Map<Integer, Date> triggers = new ConcurrentHashMap<>();

	final AtomicInteger count = new AtomicInteger(0);

	@EventListener
	public void trigger(ScheduleEvent event) {
		this.triggers.put(this.count.incrementAndGet(), event.getSource());
		log.debug("triggered @ " + Instant.now() + "!");
	}

}