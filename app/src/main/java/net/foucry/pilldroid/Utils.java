package net.foucry.pilldroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    /**
     * parse barcode result into barcodevalues
     * @param requestCode int
     * @param resultCode int
     * @param intent Intent
     * @param context Context
     * @return BarcodeValues
     */
    static public BarcodeValues parseSetBarcodeActivityResult(int requestCode, int resultCode,
                                                              Intent intent, Context context) {
      String contents = null;
      String format = null;

      if (resultCode != Activity.RESULT_OK) {
        return new BarcodeValues(null, null);
      }

      if (requestCode == Utils.BARCODE_SCAN || requestCode == Utils.SELECT_BARCODE_REQUEST) {
        if (requestCode == Utils.BARCODE_SCAN) {
          Log.d(TAG, "Received barcode information form camera");
        } else if (requestCode == Utils.SELECT_BARCODE_REQUEST) {
          Log.d(TAG, "Received barcode information form typeing it");
        }

        contents = intent.getStringExtra("BarcodeContent");
        format = intent.getStringExtra("BarcodeFormat");
      }
      return new BarcodeValues(format, contents);
    }
}
