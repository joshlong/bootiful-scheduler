package com.joshlong.scheduling.engine;

import org.springframework.context.ApplicationEvent;

import java.util.Date;

public class ScheduleEvent extends ApplicationEvent {

    public ScheduleEvent(Date source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Date getSource() {
        return (Date) super.getSource();
    }
}
