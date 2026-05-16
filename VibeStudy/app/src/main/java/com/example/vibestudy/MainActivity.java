package com.example.vibestudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcome_text);
        Button btnHowTo = findViewById(R.id.btn_how_to);

        // Open Tutorial page first
        btnHowTo.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("trueFirstTime", false).apply();

            startActivity(new Intent(MainActivity.this, InstructionsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "");
        String job = prefs.getString("userJob", "");

        // Displays the user's name and occupation once saved
        if (welcomeText != null) {
            if (name.isEmpty()) {
                welcomeText.setText("Ready to study?");
            } else {
                welcomeText.setText("Ready to study, " + name + " (" + job + ")?");
            }
        }
    }
}