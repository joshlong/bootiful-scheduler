package com.joshlong.scheduling.engine;

import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.List;

public class ScheduleRefreshEvent extends ApplicationEvent {

    public ScheduleRefreshEvent(List<Date> list) {
        super(list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Date> getSource() {
        return (List<Date>) super.getSource();
    }
}
