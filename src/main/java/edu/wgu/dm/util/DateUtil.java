package edu.wgu.dm.util;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final ZoneId SERVER_ZONEID = ZoneId.of("MST7MDT");

    public static Date getZonedNow() {
        return Date.from(ZonedDateTime.now(SERVER_ZONEID)
                                      .toInstant());
    }

    private static ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(SERVER_ZONEID);
    }

    public static Date getZonedDate(int days) {
        ZonedDateTime zdt = getZonedDateTime();
        ZonedDateTime estimatedCompletionDate = zdt.plusDays(days);
        return Date.from(estimatedCompletionDate.toInstant());
    }

    public static Date startOfDay(Date input) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date endOfDay(Date input) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
