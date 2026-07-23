package com.example.smartplaygroundbookingequipmentrentalapp;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminUserPhone;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        tvAdminUserPhone = findViewById(R.id.tvAdminUserPhone);

        refreshPhoneDisplay();

        View.OnClickListener editPhoneListener = v -> showEditPhoneDialog();
        findViewById(R.id.btnChangePhone).setOnClickListener(editPhoneListener);
        findViewById(R.id.btnEditUserPhone).setOnClickListener(editPhoneListener);

        findViewById(R.id.btnManagePlaygrounds).setOnClickListener(v -> {
            Toast.makeText(this, "Managing 14 Registered Playgrounds", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnViewReports).setOnClickListener(v -> {
            Toast.makeText(this, "Generating Analytics Report...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void refreshPhoneDisplay() {
        String phone = GlobalState.INSTANCE.getCurrentUserPhone();
        if (phone == null || phone.isEmpty()) {
            phone = sessionManager.getUserPhone();
        }
        tvAdminUserPhone.setText(phone);
    }

    private void showEditPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User Contact Phone");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(tvAdminUserPhone.getText().toString());
        input.setPadding(32, 32, 32, 32);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPhone = input.getText().toString().trim();
            if (!newPhone.isEmpty()) {
                GlobalState.INSTANCE.updatePhone(this, newPhone);
                refreshPhoneDisplay();
                Toast.makeText(this, "Phone number updated to: " + newPhone, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
