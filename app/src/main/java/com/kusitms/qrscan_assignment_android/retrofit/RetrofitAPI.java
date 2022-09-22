package com.kusitms.qrscan_assignment_android.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/validate")
    Call<ResponseResult> validateSerialNumber(@Query("serialNumber") String serialNumber);
}
