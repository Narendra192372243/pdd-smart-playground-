package com.example.smartplaygroundbookingequipmentrentalapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            com.google.firebase.FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionManager session = new SessionManager(this);

        new Handler().postDelayed(() -> {
            try {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if (session.isLoggedIn()) {
                        intent.putExtra("start_route", "home_root");
                    } else {
                        intent.putExtra("start_route", "onboarding");
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
                    // Still allow to proceed for demo, or you can choose to finish()
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error starting app", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
