package com.kusitms.qrscan_assignment_android.retrofit;

import androidx.annotation.NonNull;

public class ResponseResult {
    int statusCode;
    String responseMessage;
    String data;

    @NonNull
    @Override
    public String toString() {
        return "{ statusCode : " + statusCode + "\n" +
                "response message : " + responseMessage + " } ";
    }
}
