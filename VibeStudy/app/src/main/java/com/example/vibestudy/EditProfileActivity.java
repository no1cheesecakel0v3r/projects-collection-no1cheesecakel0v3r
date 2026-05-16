package com.example.vibestudy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This reuses your clean profile layout with the name and occupation fields
        setContentView(R.layout.activity_settings);

        EditText nameInput = findViewById(R.id.edit_user_name);
        EditText jobInput = findViewById(R.id.edit_occupation);
        Button saveBtn = findViewById(R.id.btn_save_prefs);

        SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);

        // Pre-fill the inputs with whatever the user saved previously
        nameInput.setText(prefs.getString("userName", ""));
        jobInput.setText(prefs.getString("userJob", ""));

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String job = jobInput.getText().toString().trim();

            if (!name.isEmpty() && !job.isEmpty()) {
                // Save updated details to memory
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", name);
                editor.putString("userJob", job);
                editor.apply();

                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                finish(); // This closes this screen and drops them back to the settings menu seamlessly
            } else {
                Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}