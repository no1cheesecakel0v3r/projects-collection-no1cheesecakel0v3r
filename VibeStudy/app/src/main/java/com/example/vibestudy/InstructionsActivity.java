package com.example.vibestudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        Button btnOkay = findViewById(R.id.btn_okay);

        btnOkay.setOnClickListener(v -> {
            // Send them straight to profile configuration
            Intent intent = new Intent(InstructionsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}