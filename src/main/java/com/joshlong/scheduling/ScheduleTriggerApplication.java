package com.joshlong.scheduling;

import com.joshlong.scheduling.engine.ScheduleEvent;
import com.joshlong.scheduling.engine.ScheduleRefreshEvent;
import com.joshlong.scheduling.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@SpringBootApplication
public class ScheduleTriggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleTriggerApplication.class, args);
	}

}

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
class SchedulingHttpController {

	private final ApplicationEventPublisher publisher;

	private final List<Date> dates = new CopyOnWriteArrayList<>();

	@GetMapping("/clear")
	Map<String, Object> clear() {
		this.dates.clear();
		this.publisher.publishEvent(new ScheduleRefreshEvent(this.dates));
		return Map.of("count", this.dates.size());
	}

	@GetMapping("/schedule")
	Map<String, Object> schedule() {
		var later = DateUtils.secondsLater(new Date(), 10);
		this.dates.add(later);
		this.publisher.publishEvent(new ScheduleRefreshEvent(this.dates));
		return Map.of("count", this.dates.size());
	}

	@EventListener
	public void schedule(ScheduleEvent scheduleEvent) {
		var date = scheduleEvent.getSource();
		log.info("got a callback for " + date);
	}

}
