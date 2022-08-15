package com.joshlong.scheduling.engine;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest
class DefaultSchedulingServiceTest {

	private final ApplicationEventPublisher publisher;

	private final TriggerTest test;

	private final SchedulingService schedulingService;

	DefaultSchedulingServiceTest(@Autowired TriggerTest test, @Autowired ApplicationEventPublisher publisher,
			@Autowired SchedulingService schedulingService) {
		this.publisher = publisher;
		this.test = test;
		this.schedulingService = schedulingService;
	}

	private void date(Instant instant) {
		log.info("going to trigger at " + instant.toString());
		this.publisher.publishEvent(new ScheduleRefreshEvent(List.of(instant)));
	}

	@SneakyThrows
	private static void pause(long seconds) {
		Thread.sleep(seconds * 1000);
	}

	@Test
	void schedule() {
		var now = Instant.now();
		date(now.plus(5, TimeUnit.SECONDS.toChronoUnit()));
		pause(6);
		Assertions.assertEquals(this.test.count.get(), 1);
		date(now.plus(8, TimeUnit.SECONDS.toChronoUnit()));
		var dupe = now.plus(9, TimeUnit.SECONDS.toChronoUnit());
		date(dupe);
		date(dupe);
		pause(10);
		Assertions.assertEquals(this.test.count.get(), 3); // not 4!

		this.schedulingService.schedule(List.of(now.plus(20, TimeUnit.SECONDS.toChronoUnit())));
		pause(5);
		Assertions.assertEquals(this.test.count.get(), 4);
	}

}

@SpringBootApplication
class Main {

}

@Slf4j
@Component
class TriggerTest {

	final Map<Integer, Instant> triggers = new ConcurrentHashMap<>();

	final AtomicInteger count = new AtomicInteger(0);

	@EventListener
	public void trigger(ScheduleEvent event) {
		this.triggers.put(this.count.incrementAndGet(), event.getSource());
		log.info("triggered @ " + Instant.now() + "!");
	}

}