package net.foucry.pilldroid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Random;

/**
 * Custom Scanner Activity extending from Activity to display a custom layout form scanner view.
 */
public class CustomScannerActivity extends Activity {

    private  static final String TAG = CustomScannerActivity.class.getName();

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private ViewfinderView viewfinderView;

    private ActivityResultLauncher<Intent> manualAddLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        manualAddLauncher = registerForActivityResult(new ActivityResultContract.StartActivityForResutl(),
                result -> DrugListActivity.showInputDialog());

        barcodeScannerView.setTorchListener(this);

        findViewById(R.id.keyboard_button).setOnClickListener(this::addManually);
        findViewById(R.id.cancel_button).setOnClickListener(this::onCancel);
        // viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        Intent captureIntent = new Intent();
        Bundle captureIntentBundle = new Bundle();
        captureIntentBundle.putBoolean(Intents.Scan.BEEP_ENABLED, true);
        captureIntent.putExtras(captureIntentBundle);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        //capture.decode();

        changeMaskColor(null);
        changeLaserVisibility(true);
        barcodeScannerView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                Intent scanResult = new Intent();
                Bundle scanResultBundle = new Bundle();
                scanResultBundle.putString("Barcode Content", result.getText());
                scanResultBundle.putString("Barcode Format name", result.getBarcodeFormat().name());
                scanResultBundle.putInt("returnCode", captureIntentBundle.getInt("returnCode"));
                scanResult.putExtras(scanResultBundle);
                CustomScannerActivity.this.setResult(RESULT_OK, scanResult);
                finish();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
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

    @Override
    public void onTorchOn() {
        Log.d(TAG, "TorchON");
        switchFlashlightButton.setActivated(true);
    }

    @Override
    public void onTorchOff() {
        Log.d(TAG, "TorchOFF");
        switchFlashlightButton.setActivated(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode);
        
    }
    public void onKeyboard(View view) {
        Log.d(TAG, "onkeyboard");
        captureIntentBundle.putInt("returnCode", 3);
    }

    public void onCancel(View view) {
        Log.d(TAG, "onCancel");
        captureIntentBundle.putInt("returnCode", 2);
    }
}
