package demo;

import com.joshlong.scheduling.engine.ScheduleEvent;
import com.joshlong.scheduling.engine.ScheduleRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class Main {

	private final ApplicationEventPublisher publisher;

	public Main(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void begin() {
		var then = Instant.now().plus(5, TimeUnit.SECONDS.toChronoUnit());
		this.publisher.publishEvent(new ScheduleRefreshEvent(List.of(then)));
	}

	@EventListener
	public void onSchedule(ScheduleEvent scheduleEvent) {
		log.info("scheduled: " + scheduleEvent.getSource());
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
