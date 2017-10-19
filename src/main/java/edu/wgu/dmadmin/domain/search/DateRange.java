package edu.wgu.dmadmin.domain.search;

public class DateRange {
	public static final int timeframe24Hours = -1;
	public static final int timeframe72Hours = -3;
	public static final int timeframe7Days = -7;
	public static final int timeframe30Days = -30;
	
	public static final String TIMEFRAME_24_HOURS = "timeframe24Hours";
	public static final String TIMEFRAME_72_HOURS = "timeframe72Hours";
	public static final String TIMEFRAME_7_DAYS = "timeframe7Days";
	public static final String TIMEFRAME_30_DAYS = "timeframe30Days";
	public static final String TIMEFRAME_ANY = "timeframeAny";
	
	public static int getDaysForDateRange(String range) {
		switch (range) {
		case TIMEFRAME_24_HOURS:
			return timeframe24Hours;
		case TIMEFRAME_72_HOURS:
			return timeframe72Hours;
		case TIMEFRAME_7_DAYS:
			return timeframe7Days;
		case TIMEFRAME_30_DAYS:
			return timeframe30Days;
		default:
			throw new IllegalArgumentException();
		}
	}
}
