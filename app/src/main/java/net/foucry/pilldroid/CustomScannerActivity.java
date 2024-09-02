package net.foucry.pilldroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

/**
 * Custom Scanner Activity extending from Activity to display a custom layout form scanner view.
 */
public class CustomScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private static final String TAG = CustomScannerActivity.class.getName();
    final Bundle captureIntentBundle = new Bundle();
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private ViewfinderView viewfinderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_scanner_activty);

        //barcodeScannerView.setTorchListener(this);

        findViewById(R.id.keyboard_button).setOnClickListener(this::onKeyboard);
        findViewById(R.id.cancel_button).setOnClickListener(this::onCancel);
        findViewById(R.id.switch_flashlight).setOnClickListener(this::switchFlashlight);
        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            findViewById(R.id.switch_flashlight).setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);

        //changeMaskColor(null);
        changeLaserVisibility(true);
        barcodeScannerView.decodeSingle(result -> {
            Intent scanResult = new Intent();
            //Bundle scanResultBundle = new Bundle();
            scanResult.putExtra("Barcode Content", result.getText());
            scanResult.putExtra("Barcode Format name", result.getBarcodeFormat().name());
            scanResult.putExtra("returnCode", captureIntentBundle.getInt("returnCode"));
            scanResult.putExtra("resultCode", 1);
            CustomScannerActivity.this.setResult(RESULT_OK, scanResult);
            Log.d(TAG, "scanResult == " + scanResult);
            finish();
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
    public void onBackPressed() {
        super.onBackPressed();
        onCancel(this.getCurrentFocus());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
            switchFlashlightButton.setActivated(false);
        } else {
            barcodeScannerView.setTorchOn();
            switchFlashlightButton.setActivated(true);
        }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onKeyboard(View view) {
        Log.d(TAG, "onkeyboard");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnCode", 3);
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
