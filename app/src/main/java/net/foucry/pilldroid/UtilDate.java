package net.foucry.pilldroid;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jacques on 05/05/16.
 */
class UtilDate {

    private static final String TAG = UtilDate.class.getName();
    /**
     *
     * @param aDate anydate
     * @return date the same date as input but at noon (12:00:00)
     *
     * set date time at Noon
     */
    static Date dateAtNoon(Date aDate) {

//        Log.d(TAG, "dateAtNoon " + aDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aDate);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
    /**
     *
     * @param days number of days to remove to the ate
     * @param date date before day removing
     * @return date
     *
     * Substract days to date and return a new date
     */
    static Date removeDaysToDate(int days, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -days);

        return calendar.getTime();
    }

    /**
     *
     * @param date Date to be converted
     * @return String of the converted date
     *
     * Convert a date to a String using a SimpleDateFormat
     */
    static String date2String(Date date, DateFormat dateFormat) {

        Log.d(TAG, "date == " + date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return dateFormat.format(calendar.getTime());
    }

    /**
     *
     * @param dateString string representing a Date to be conveted
     * @return date Date after convertion
     *
     * Convert String date into Date
     */
    static Date string2Date(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        ParsePosition pos = new ParsePosition(0);
        return dateFormat.parse(dateString,pos);
    }

    /**
     *
     * @param date start date
     * @return int numbers of days between date and today
     *
     * Number of days between date (older than today) and today
     */
    static int nbOfDaysBetweenDateAndToday(Date date) {
        Date oldDate = dateAtNoon(date); // Be sure that the old date is at Noon
        Date todayDate = dateAtNoon(new Date()); // Be sure that we use today at Noon

        return (int) (todayDate.getTime() - oldDate.getTime());
    }

    /**
     * @param: none
     * return int
     */

    static long tomorrowAtNoonInMillis() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR,1);

        Date tomorrowAtNoon = dateAtNoon(calendar.getTime());

        return (tomorrowAtNoon.getTime() - now.getTime());
    }

    static String convertDate(long dateInMilliseconds) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMilliseconds);
        return formatter.format(calendar.getTime());
    }
}
