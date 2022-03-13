package net.foucry.pilldroid;

import java.util.Locale;
import java.util.Random;

public class Utils {
    private static final String TAG = Utils.class.getName();

    /**
     * Return a random number between two values - use to generate a false demo DB
     * @param min minimal value accepted
     * @param max maximum value accepted
     * @return int random number
     */
    static int intRandomExclusive(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) +max;
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format(Locale.getDefault(),"%d",(long)d);
        else
            return String.format("%s",d);
    }
}
