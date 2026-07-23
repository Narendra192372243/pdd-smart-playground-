package com.example.smartplaygroundbookingequipmentrentalapp.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
