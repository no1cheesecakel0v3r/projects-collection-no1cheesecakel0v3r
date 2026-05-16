package com.example.vibestudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText nameInput = findViewById(R.id.edit_user_name);
        EditText jobInput = findViewById(R.id.edit_occupation);
        Button saveBtn = findViewById(R.id.btn_save_prefs);

        SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);

        // Load existing values if they exist
        nameInput.setText(prefs.getString("userName", ""));
        jobInput.setText(prefs.getString("userJob", ""));

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String job = jobInput.getText().toString().trim();

            if (!name.isEmpty() && !job.isEmpty()) {
                // Save both details to memory
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", name);
                editor.putString("userJob", job);
                editor.apply();

                Toast.makeText(this, "Profile Created!", Toast.LENGTH_SHORT).show();

                // LAUNCH THE BRAND NEW DASHBOARD
                Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}