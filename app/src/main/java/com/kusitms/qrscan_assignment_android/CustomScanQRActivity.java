package com.kusitms.qrscan_assignment_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.kusitms.qrscan_assignment_android.databinding.ActivityCustomScanQrBinding;

public class CustomScanQRActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private ActivityCustomScanQrBinding binding;

    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton btnSwitchFlash;
    private Boolean switchFlashlightButtonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomScanQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        switchFlashlightButtonCheck = true;

        btnSwitchFlash = binding.btnFlash;
        barcodeScannerView = binding.decoratedBarcodeView;

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

        binding.btnCamera.setOnClickListener(v -> {
            changeCamera();
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
        btnSwitchFlash.setImageResource(R.drawable.ic_flash);
        switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        btnSwitchFlash.setImageResource(R.drawable.ic_no_flash);
        switchFlashlightButtonCheck = true;
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void changeCamera() {
        onPause();

        CameraSettings cameraSettings = barcodeScannerView.getCameraSettings();
        if(cameraSettings.getRequestedCameraId() == 1) {
            cameraSettings.setRequestedCameraId(0);
            barcodeScannerView.setCameraSettings(cameraSettings);
        } else {
            cameraSettings.setRequestedCameraId(1);
            barcodeScannerView.setCameraSettings(cameraSettings);
        }

        onResume();
    }
}