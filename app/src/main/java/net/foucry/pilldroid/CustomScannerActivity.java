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

    private static final String TAG = CustomScannerActivity.class.getName();

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private ViewfinderView viewfinderView;

    final Intent captureIntent = new Intent();
    final Bundle captureIntentBundle = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_scanner);
/*        manualAddLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result.getResultCode(),
                        result.getData()));*/

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

        captureIntentBundle.putBoolean(Intents.Scan.BEEP_ENABLED, true);
        captureIntentBundle.putInt("Intents.Scan.MIXED_SCAN", Intents.Scan.MIXED_SCAN);
        captureIntentBundle.putInt("Intents.Scan.INVERTED_SCAN", Intents.Scan.INVERTED_SCAN);

        captureIntent.putExtras(captureIntentBundle);

        /*captureIntent.putExtra("Intents.Scan.MIXED_SCAN", Intents.Scan.MIXED_SCAN);
        captureIntent.putExtra("Intents.Scan.INVERTED_SCAN", Intents.Scan.INVERTED_SCAN);
        captureIntent.putExtra("Intents.Scan.BEEP_ENABLED", Intents.Scan.BEEP_ENABLED);*/

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        //capture.decode();

        changeMaskColor(null);
        changeLaserVisibility(true);
        barcodeScannerView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                Intent scanResult = new Intent();
                //Bundle scanResultBundle = new Bundle();
                scanResult.putExtra("Barcode Content", result.getText());
                scanResult.putExtra("Barcode Format name", result.getBarcodeFormat().name());
                scanResult.putExtra("returnCode", captureIntentBundle.getInt("returnCode"));
                scanResult.putExtra("resultCode", 1);
                CustomScannerActivity.this.setResult(RESULT_OK, scanResult);
                Log.d(TAG, "scanResult == " + scanResult);
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

    /*private void handleActivityResult(int resultCode, Intent intent) {
        super.onActivityResult(Utils.SELECT_BARCODE_REQUEST, resultCode, intent);

        BarcodeValues barcodeValues;

        try {
            barcodeValues = Utils.parseSetBarcodeActivityResult(Utils.SELECT_BARCODE_REQUEST, resultCode, intent, this);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Error reading image", Toast.LENGTH_LONG).show();
            return;
        }

        if (!barcodeValues.isEmpty()) {
            Intent manualResult = new Intent();
            Bundle manualResultBundle = new Bundle();
            manualResultBundle.putString("BarcodeContent", barcodeValues.content());
            manualResultBundle.putString("BarcodeFormat", barcodeValues.format());

            manualResult.putExtras(manualResultBundle);
            CustomScannerActivity.this.setResult(RESULT_OK, manualResult);
            finish();
        }
    }*/
}
