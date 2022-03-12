package net.foucry.pilldroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Random;

public class Utils {
    private static final String TAG = Utils.class.getName();

    public static final int SELECT_BARCODE_REQUEST = 2;
    public static final int BARCODE_SCAN = 3;

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return a random number between twovalues - use to gənerate a false demo DB
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
