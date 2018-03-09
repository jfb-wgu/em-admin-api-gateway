package edu.wgu.dmadmin.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
	public static final ZoneId SERVER_ZONEID = ZoneId.of("MST7MDT");
	
	public static Date getZonedNow() {
		return Date.from(ZonedDateTime.now(SERVER_ZONEID).toInstant());
	}
	
	public static ZonedDateTime getZonedDateTime() {
		return ZonedDateTime.now(SERVER_ZONEID);
	}
	
	public static Date getZonedDate(int days) {
		ZonedDateTime zdt = getZonedDateTime();
		ZonedDateTime estimatedCompletionDate = zdt.plusDays(days);
		return Date.from(estimatedCompletionDate.toInstant());
	}
}
