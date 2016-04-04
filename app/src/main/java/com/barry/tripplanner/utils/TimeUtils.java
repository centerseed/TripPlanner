package com.barry.tripplanner.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static int daysBetween(String start, String end) {
        try {
            String starts[] = start.split("-");
            String ends[] = end.split("-");
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(new Date(Integer.valueOf(starts[0]) - 1900, Integer.valueOf(starts[1]) - 1, Integer.valueOf(starts[2])));
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(new Date(Integer.valueOf(ends[0]) - 1900, Integer.valueOf(ends[1]) - 1, Integer.valueOf(ends[2])));
            return daysBetween(startDate, endDate);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) + 1;
    }
}
