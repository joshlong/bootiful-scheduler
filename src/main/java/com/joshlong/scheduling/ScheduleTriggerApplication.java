package com.joshlong.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@SpringBootApplication
public class ScheduleTriggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleTriggerApplication.class, args);
    }

    private static Date secondsInTheFuture(Instant now, int seconds) {
        return Date.from(now.plus(seconds, TimeUnit.SECONDS.toChronoUnit()));
    }

    @Bean
    ApplicationRunner applicationRunner(TaskScheduler scheduler) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {

                scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, Instant.now().plus(10, TimeUnit.SECONDS.toChronoUnit()));
            }
        };
    }
}

@Configuration
class SchedulingConfiguration {


    @Bean
    TaskScheduler taskScheduler() {
        return new TaskSchedulerBuilder()
                .poolSize(10)
                .build();
    }

    @Bean
    ScheduleTrigger scheduleTrigger() {
        return new ScheduleTrigger(List.of());
    }

    @Slf4j
    private static class SimpleRunnable implements Runnable {

        @Override
        public void run() {
            log.info("running @ " + Instant.now());
        }
    }

    @Bean
    InitializingBean scheduleInitializer( ScheduleService ss ) {
        return () -> {
            ss.begin();
        };
    }

}

@Service
@RequiredArgsConstructor
class ScheduleService implements Runnable {

    private final ScheduleTrigger trigger;
    private final TaskScheduler scheduler;

    void begin() {
        var scheduledFuture = this.scheduler.schedule(this, this.trigger);

    }

    @Override
    public void run() {

    }
}

@Slf4j
class ScheduleTrigger implements Trigger {

    private final AtomicReference<List<Date>> scheduledList = new AtomicReference<>();
    private final AtomicInteger offset = new AtomicInteger(0);

    ScheduleTrigger(List<Date> scheduledList) {
        this.refresh(scheduledList);
    }

    private List<Date> reviseOffset(List<Date> schedules, Date now) {
        return schedules
                .stream()
                .distinct()
                .sorted(Date::compareTo)
                .filter(d -> d.after(now))
                .toList();
    }

    public void refresh(List<Date> scheduledList) {
        var now = new Date();
        var scheduleList = this.reviseOffset(scheduledList, now);
        this.scheduledList.set(scheduleList);
        this.offset.set(0);
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        log.debug("nextExecutionTime: ");
        var list = this.scheduledList.get();
        var currentOffset = this.offset.get();
        var length = list.size();

        if (currentOffset < length) {
            this.offset.incrementAndGet();
            return list.get(currentOffset);
        } //
        else {
            this.offset.set(0);
            return null;
        }
    }
}