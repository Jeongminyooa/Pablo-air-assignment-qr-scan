package com.kusitms.qrscan_assignment_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.kusitms.qrscan_assignment_android.databinding.ActivityMainBinding;
import com.kusitms.qrscan_assignment_android.retrofit.ResponseResult;
import com.kusitms.qrscan_assignment_android.retrofit.RetrofitAPI;
import com.kusitms.qrscan_assignment_android.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ScanOptions scanOptions;

    private RetrofitAPI retrofitAPI;

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
                    RetrofitClient retrofitClient = RetrofitClient.getInstance();

                    retrofitAPI = RetrofitClient.getRetrofitAPI();
                    retrofitAPI.validateSerialNumber(result.getContents()).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            if(response.isSuccessful()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("인증에 성공했습니다.")
                                        .setMessage("잠금 장치가 해제되었습니다.\n배송지에서 물품을 가져가세요.")
                                        .create()
                                        .show();
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("인증에 실패했습니다.")
                                        .setMessage("주문자의 QR 코드가 맞는지 확인하세요.")
                                        .create()
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {

                        }
                    });
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