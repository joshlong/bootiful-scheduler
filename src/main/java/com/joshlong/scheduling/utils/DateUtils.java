package com.joshlong.scheduling.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class DateUtils {

	public static Date secondsLater(Date now, int seconds) {
		return Date.from(now.toInstant().plus(seconds, TimeUnit.SECONDS.toChronoUnit()));
	}

}
