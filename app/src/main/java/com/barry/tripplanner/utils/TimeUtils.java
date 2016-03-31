package com.barry.tripplanner.utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static int daysBetween(String start, String end) {
        try {
            String starts[] = start.split("-");
            String ends[] = end.split("-");
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();

            startDate.set(Calendar.YEAR, Integer.valueOf(starts[0]),
                    Calendar.MONTH, Integer.valueOf(starts[1]),
                    Calendar.DAY_OF_MONTH, Integer.valueOf(starts[2]));

            endDate.set(Calendar.YEAR, Integer.valueOf(ends[0]),
                    Calendar.MONTH, Integer.valueOf(ends[1]),
                    Calendar.DAY_OF_MONTH, Integer.valueOf(ends[2]));
            return daysBetween(startDate, endDate);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
}
