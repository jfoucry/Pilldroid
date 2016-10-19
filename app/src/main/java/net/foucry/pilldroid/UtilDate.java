package net.foucry.pilldroid;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by jacques on 05/05/16.
 */
public class UtilDate {

    private static final String TAG = UtilDate.class.getName();
    /**
     *
     * @param aDate
     * @return date
     *
     * set date time at Noon
     */
    public static Date dateAtNoon(Date aDate) {

        Log.d(TAG, "dateAtNoon " + aDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(aDate);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
    /**
     *
     * @param days
     * @param date
     * @return date
     *
     * Substract days to date and return a new date
     */
    public static Date removeDaysToDate(int days, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -days);

        return calendar.getTime();
    }

    /**
     *
     * @param date
     * @return String
     *
     * Convert a date to a String using a SimpleDateFormat
     */
    public static String date2String(Date date, DateFormat dateFormat) {

        Log.d(TAG, "date == " + date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return dateFormat.format(calendar.getTime());
    }

    /**
     *
     * @param dateString
     * @return date
     *
     * Convert String date into Date
     */
    public static Date string2Date(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        return dateFormat.parse(dateString,pos);
    }

    /**
     *
     * @param date
     * @return int
     *
     * Number of days between date (older than today) and today
     */
    public static int nbOfDaysBetweenDateAndToday(Date date) {
        Date oldDate = dateAtNoon(date); // Be sure that the old date is at Noon
        Date todayDate = dateAtNoon(new Date()); // Be sure that we use today at Noon

        return (int) (todayDate.getTime() - oldDate.getTime());
    }

    /**
     * @param: none
     * return int
     */

    public static long tomorrowAtNoonInMillis() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR,1);

        Date tomorrowAtNoon = dateAtNoon(calendar.getTime());

        return (tomorrowAtNoon.getTime() - now.getTime());
    }

    public static String convertDate(long dateInMilliseconds) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMilliseconds);
        return formatter.format(calendar.getTime());
    }
}
