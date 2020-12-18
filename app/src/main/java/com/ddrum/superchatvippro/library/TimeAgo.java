package com.ddrum.superchatvippro.library;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int MONTH_MILLIS = 30 * HOUR_MILLIS;

    public static String getTimeAgo(String online) {
        if (online.equals("true")) {
            return "Đang online";
        }

        long time = Long.parseLong(online);
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Vừa xong";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 phút trước";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "1 giờ trước";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "hôm qua";
        } else {
            return diff / DAY_MILLIS + " ngày trước";
        }
    }

    public static String getTime(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        final long diff = now - timestamp;

        String timeFormat;
        if (diff < 24 * HOUR_MILLIS) {
            timeFormat = "h:mm a ";
        } else if (diff / DAY_MILLIS < 7) {
            timeFormat = "HH:mm, " +
                    "EEE";
        } else if (diff / DAY_MILLIS < 365) {
            timeFormat = "d MMM";
        } else {
            timeFormat = "d MMM yy";
        }


        DateFormat dateFormat = new SimpleDateFormat(timeFormat);
        return dateFormat.format(timestamp);
    }
}
