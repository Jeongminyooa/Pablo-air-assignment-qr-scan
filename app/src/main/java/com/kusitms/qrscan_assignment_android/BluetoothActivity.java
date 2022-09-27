package com.kusitms.qrscan_assignment_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.kusitms.qrscan_assignment_android.databinding.ActivityBluetoothBinding;
import com.kusitms.qrscan_assignment_android.retrofit.ResponseResult;
import com.kusitms.qrscan_assignment_android.retrofit.RetrofitAPI;
import com.kusitms.qrscan_assignment_android.retrofit.RetrofitClient;
import com.kusitms.qrscan_assignment_android.util.AES256Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "Bluetooth Activity";
    private ActivityBluetoothBinding binding;

    TextView mTvBluetoothStatus;
    Button mBtnBluetoothOn;
    Button mBtnBluetoothOff;
    Button mBtnConnect;


    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private List<String> mListPairedDevices;

    private ConnectedBluetoothThread mThreadConnectedBluetooth;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ScanOptions scanOptions;
    private RetrofitAPI retrofitAPI;

    private AES256Util aes256Util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        aes256Util = new AES256Util();

        scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(false)
                .setCaptureActivity(CustomScanQRActivity.class)
                .setCameraId(0)
                .setPrompt("QR 코드를 스캔하면 잠금 장치가 해제됩니다.");

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    1);
        }

        mTvBluetoothStatus = binding.tvBluetoothStatus;
        mBtnBluetoothOn = binding.btnBluetoothOn;
        mBtnBluetoothOff = binding.btnBluetoothOff;
        mBtnConnect = binding.btnConnect;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // click event
        mBtnBluetoothOn.setOnClickListener(v -> {
            bluetoothOn();
        });
        mBtnBluetoothOff.setOnClickListener(v -> {
            bluetoothOff();
        });
        mBtnConnect.setOnClickListener(v -> {
            listPairedDevices();
        });

        binding.btnQRScan.setOnClickListener(v -> {
            barcodeLauncher.launch(scanOptions);
        });
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Permission approved");
                } else {
                    Log.d(TAG, "Error getting permission");
                }
                return;
        }

    }

    // 블루투스 활성화 메서드
    private void bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("활성화");
            } else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothLauncher.launch(intentBluetoothEnable);
            }
        }
    }

    ActivityResultLauncher<Intent> bluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                        Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                        mTvBluetoothStatus.setText("활성화");
                    } else if (result.getResultCode() == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                        mTvBluetoothStatus.setText("비활성화");
                    }
                }
            });

    // 블루투스 비활성화 메소드
    void bluetoothOff() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();

            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            mTvBluetoothStatus.setText("비활성화");
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    // 블루투스 페어링 장치 불러오는 메서드
    void listPairedDevices() {
        // 블루투스 활성화 상태인지 확인
        if (mBluetoothAdapter.isEnabled()) {
            // 페어링 된 장치가 존재한다면
            // 새로운 알람창 객체를 생성하여 알람창에 "장치선택" 타이틀과 각 페어링된 장치명을 추가해
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }

                //페어링된 장치 수를 얻어와서 각 장치를 누르면 장치 명을 매개변수로 사용하여
                // connectSelectedDevice 메서드로 전달해주는 클릭 이벤트를 추가
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 블루투스 연결하는 메서드
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    // 데이터 송수신 스레드
    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
    // QR 스캔
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d(TAG, "Cancelled scan");
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d(TAG, "Cancelled scan due to missing camera permission");
                    }
                } else {
                    // 클라이언트키로 암호화된 시리얼 넘버
                    Log.d(TAG, "Scanned. SerialNumber : " + result.getContents());

                    // 복호화 - 암호화
                    String serialNumber = aes256Util.transferKey(result.getContents());

                    RetrofitClient retrofitClient = RetrofitClient.getInstance();

                    retrofitAPI = RetrofitClient.getRetrofitAPI();
                    retrofitAPI.validateSerialNumber(serialNumber).enqueue(new Callback<ResponseResult>() {
                        @Override
                        public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, response.body().toString());

                                new android.app.AlertDialog.Builder(BluetoothActivity.this)
                                        .setTitle("인증에 성공했습니다.")
                                        .setMessage("잠금 장치가 해제되었습니다.\n배송지에서 물품을 가져가세요.")
                                        .setPositiveButton("잠금 해제", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (mThreadConnectedBluetooth != null) {
                                                    mThreadConnectedBluetooth.write("1");
                                                }
                                            }
                                        })
                                        .create()
                                        .show();
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                new android.app.AlertDialog.Builder(BluetoothActivity.this)
                                        .setTitle("인증에 실패했습니다.")
                                        .setMessage("주문자의 QR 코드가 맞는지 확인하세요.")
                                        .create()
                                        .show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseResult> call, Throwable t) {
                            Log.e(TAG, "통신 오류" + t.getMessage());
                        }
                    });
                }
            });
}