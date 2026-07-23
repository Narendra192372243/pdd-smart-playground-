package com.example.smartplaygroundbookingequipmentrentalapp.api;

import com.example.smartplaygroundbookingequipmentrentalapp.response.BaseResponse;
import com.example.smartplaygroundbookingequipmentrentalapp.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {
    @FormUrlEncoded
    @POST("register.php")
    Call<BaseResponse> registerUser(
        @Field("name") String name,
        @Field("email") String email,
        @Field("phone") String phone,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> loginUser(
        @Field("email") String email,
        @Field("password") String password
    );

    @GET("get_grounds.php")
    Call<BaseResponse> getGrounds();
}
