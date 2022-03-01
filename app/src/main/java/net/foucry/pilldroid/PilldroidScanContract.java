package net.foucry.pilldroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;


public class PilldroidScanContract extends ActivityResultContract<ScanOptions, ScanIntentResult>{
  private  static final String TAG = PilldroidScanContract.class.getName();
  @NonNull
  @Override
  public Intent createIntent(@NonNull Context context, ScanOptions input) {
    Log.d(TAG, "create Intent");

    Intent intent = new Intent(context, CustomScannerActivity.class);

    intent.setAction(Intents.Scan.ACTION);
    intent.putExtra(Intents.Scan.BEEP_ENABLED, true);
    intent.putExtra(Intents.Scan.SCAN_TYPE, 2);
    Log.d(TAG, "intent ==" + intent);
    return(intent);
  }

  @Override
  public ScanIntentResult parseResult(int resultCode, @Nullable Intent intent) {
   return ScanIntentResult.parseActivityResult(resultCode, intent);
  }
}
