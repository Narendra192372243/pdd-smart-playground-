package com.example.smartplaygroundbookingequipmentrentalapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SmartPlaygroundPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_LOCATION = "userLocation";
    private static final String KEY_USER_PROFILE_IMAGE = "userProfileImage";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String phone) {
        createLoginSession(userId, "Narendra Reddy", "narendrareddyk742@gmail.com", phone, "Adyar, Chennai");
    }

    public void createLoginSession(String userId, String name, String email, String phone, String location) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name != null ? name : "Narendra Reddy");
        editor.putString(KEY_USER_EMAIL, email != null ? email : "narendrareddyk742@gmail.com");
        editor.putString(KEY_USER_PHONE, phone != null ? phone : "+91 9876543210");
        editor.putString(KEY_USER_LOCATION, location != null ? location : "Adyar, Chennai");
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Narendra Reddy");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "narendrareddyk742@gmail.com");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "+91 9876543210");
    }

    public String getUserLocation() {
        return pref.getString(KEY_USER_LOCATION, "Adyar, Chennai");
    }

    public String getUserProfileImage() {
        return pref.getString(KEY_USER_PROFILE_IMAGE, null);
    }

    public void updateUserProfileImage(String uriString) {
        editor.putString(KEY_USER_PROFILE_IMAGE, uriString);
        editor.commit();
    }

    public void updateUserPhone(String phone) {
        editor.putString(KEY_USER_PHONE, phone);
        editor.commit();
    }

    public void updateUserLocation(String location) {
        editor.putString(KEY_USER_LOCATION, location);
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
