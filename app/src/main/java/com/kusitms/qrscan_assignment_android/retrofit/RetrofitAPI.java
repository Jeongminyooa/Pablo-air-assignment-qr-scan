package com.kusitms.qrscan_assignment_android.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @POST("/order/validate")
    Call<ResponseResult> validateSerialNumber(@Body String serialNumber);
}
