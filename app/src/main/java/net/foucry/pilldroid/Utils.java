package net.foucry.pilldroid;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;
import java.util.Random;
import java.lang.Math;

public class Utils {
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
        }
        catch(Exception ex){}
    }

    public static final double doubleRandomInclusive(int min, int max) {
        double value = Math.floor(min + (max - min) * MedicamentListActivity.random.nextDouble() *4)/4;

        return value;
    }
}