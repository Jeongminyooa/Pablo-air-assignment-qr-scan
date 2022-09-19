package com.kusitms.qrscan_assignment_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerActivity extends AppCompatActivity {

    private ScanOptions scanOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(false);
        scanOptions.setCaptureActivity(CustomScanQRActivity.class);
        scanOptions.setPrompt("QR 코드를 스캔하면 잠금 장치가 해제됩니다.");
        barcodeLauncher.launch(scanOptions);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("ScannerActivity", "Cancelled scan");
                        Toast.makeText(ScannerActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("ScannerActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(ScannerActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("ScannerActivity", "Scanned");
                    Toast.makeText(ScannerActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });
}