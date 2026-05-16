package com.example.vibestudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        // Binding your list link zones
        LinearLayout profileLink = findViewById(R.id.ll_profile_settings);
        LinearLayout howToLink = findViewById(R.id.ll_how_to_settings);
        ImageButton btnBack = findViewById(R.id.btn_back_settings);

        // Back button closes menu
        btnBack.setOnClickListener(v -> finish());

        // Profile link opens your profile editor
        profileLink.setOnClickListener(v -> {
            startActivity(new Intent(SettingsMenuActivity.this, EditProfileActivity.class));
        });

        // How To link opens your tutorial
        howToLink.setOnClickListener(v -> {
            startActivity(new Intent(SettingsMenuActivity.this, InstructionsActivity.class));
        });
    }
}