package com.joshlong.scheduling.utils;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

class DateUtilsTest {

	@Test
	void normalize() {
		var now = Instant.now();
		var anHourFromNow = Date.from(now.plus(1, TimeUnit.HOURS.toChronoUnit()));
		var up = DateUtils.normalizeDateUp(anHourFromNow, TimeUnit.HOURS.toChronoUnit());
		System.out.println(up.toString());
	}

}