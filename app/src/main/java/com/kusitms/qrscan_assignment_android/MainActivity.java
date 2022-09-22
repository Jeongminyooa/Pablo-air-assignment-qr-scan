package com.kusitms.qrscan_assignment_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.kusitms.qrscan_assignment_android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ScanOptions scanOptions;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("ScannerActivity", "Cancelled scan");
                        // todo
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("ScannerActivity", "Cancelled scan due to missing camera permission");
                        // todo
                    }
                } else {
                    Log.d("ScannerActivity", "Scanned");
                    // todo
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(false)
                .setCaptureActivity(CustomScanQRActivity.class)
                .setCameraId(0)
                .setPrompt("QR 코드를 스캔하면 잠금 장치가 해제됩니다.");

        binding.btnQRScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeLauncher.launch(scanOptions);
            }
        });
    }
}