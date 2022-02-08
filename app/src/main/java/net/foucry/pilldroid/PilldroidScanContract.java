package net.foucry.pilldroid;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;


public class PilldroidScanContract extends ActivityResultContract<ScanOptions, ScanIntentResult>{
  private  static final String TAG = PilldroidScanContract.class.getName();
  @NonNull
  @Override
  public Intent createIntent(@NonNull Context context, ScanOptions input) {
    return input.createScanIntent(context);
  }

  @Override
  public ScanIntentResult parseResult(int resultCode, @Nullable Intent intent) {
   return ScanIntentResult.parseActivityResult(resultCode, intent);
  }
}
