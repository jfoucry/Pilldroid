package net.foucry.pilldroid;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScanContract extends ActivityResultContract<ScanOptions, ScanIntentResult> {

    private static final String TAG = ScanContract.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      findViewById(R.id.keyboard_button).setOnClickListener(this::onkeyboard);
      findViewById(R.id.cancel_button).setOnClickListener(this::onCancel);
      findViewById(R.id.switch_flashlight).setOnClickListener(this::switchFlashLight);
      viewfinderView = findViewById(R.id.zxing_viewfinder_view);

      barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

      capture = new CaptureManager(this, barcodeScannerView);
      Intent captureIntent = createIntent(this, options);
      Bundle captureIntentBundle = new Bundle();

      captureIntentBundle.putBoolean(Intents.Scan.BEEP_ENABLED, true);
      captureIntent.putExtras(captureIntentBundle);
      capture.initializeFromIntent(captureIntent, savedInstanceState);

      changeMaskColor(null);
      changeLaserVisibility(true);

      barcodeScannerView.decodeSingle(new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
          Intent scanResult = new Intent();
          scanResult.putExtra("Barcode Content", result.getText());
          scanResult.putExtra("Barcode Forma name", result.getBarcodeFormat().name());
          scanResult.putExtra("returnCode", capture.captureIntentBundle.getInt("returnCode"));
          scanResult.putExtra("resultCode", 1);
          ScannerContract.this.setResult(RESULT_OK, scanResult);
          
          Log.d(TAG, "scanResult ==" + scanResult.toString());
          finish();
        }
      });
    }
    
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, ScanOptions input) {
        return input.createScanIntent(context);
    }

    @Override
    public ScanIntentResult parseResult(int resultCode, @Nullable Intent intent) {
        return ScanIntentResult.parseActivityResult(resultCode, intent);
    }

        @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    
    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        Log.d(TAG, "Switch torch");
        if (switchFlashlightButton.isActivated()) {
            barcodeScannerView.setTorchOff();
        } else {
            barcodeScannerView.setTorchOn();
        }
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }
  
    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(visible);
    }

    public void onTorchOn() {
        Log.d(TAG, "TorchON");
        switchFlashlightButton.setActivated(true);
    }

    public void onTorchOff() {
        Log.d(TAG, "TorchOFF");
        switchFlashlightButton.setActivated(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onKeyboard(View view) {
        Log.d(TAG, "onkeyboard");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCode",3);
        CustomScannerActivity.this.setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onCancel(View view) {
        Log.d(TAG, "onCancel");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCode", 2);
        CustomScannerActivity.this.setResult(RESULT_OK, resultIntent);
        finish();
    }
}
