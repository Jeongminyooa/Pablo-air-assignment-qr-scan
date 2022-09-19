package com.kusitms.qrscan_assignment_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CustomScanQRActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton btnSwitchFlash;
    private Boolean switchFlashlightButtonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scan_qr);

        switchFlashlightButtonCheck = true;

        btnSwitchFlash = findViewById(R.id.btnNoFlash);
        barcodeScannerView = findViewById(R.id.decoratedBarcodeView);

        if(!hasFlash()) {
            btnSwitchFlash.setVisibility(View.GONE);
        }

        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        btnSwitchFlash.setOnClickListener(v -> {
            if (switchFlashlightButtonCheck) {
                barcodeScannerView.setTorchOn();
            } else {
                barcodeScannerView.setTorchOff();
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    /**
     * TorchListener
     */
    @Override
    public void onTorchOn() {
        btnSwitchFlash.setImageResource(R.drawable.no_flash);
        switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        btnSwitchFlash.setImageResource(R.drawable.no_flash);
        switchFlashlightButtonCheck = true;
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}