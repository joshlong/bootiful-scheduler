package com.joshlong.scheduling.utils;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class DateUtils {

	public static Date secondsLater(Date now, int seconds) {
		return Date.from(now.toInstant().plus(seconds, TimeUnit.SECONDS.toChronoUnit()));
	}

	public static Date normalizeDateUp(Date date, TemporalUnit temporalUnit) {
		return Date.from(normalizeDateUp(date.toInstant(), temporalUnit));
	}

	public static Instant normalizeDateUp(Instant instant, TemporalUnit unit) {
		return instant.plus(unit.getDuration().dividedBy(2)).truncatedTo(unit);
	}

}
