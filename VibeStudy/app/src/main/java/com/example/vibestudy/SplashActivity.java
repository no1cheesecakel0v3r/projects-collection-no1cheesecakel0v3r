package com.example.vibestudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Binding your constellation star assets
        ImageView s1 = findViewById(R.id.star1_base);
        ImageView glow = findViewById(R.id.star1_glow);
        ImageView s2 = findViewById(R.id.star2_base);
        ImageView s3 = findViewById(R.id.star3_base);
        ImageView s4 = findViewById(R.id.star4_base);
        ImageView s5 = findViewById(R.id.star5_base);
        TextView title = findViewById(R.id.study_vibes_text);

        // Cinematic Animation Sequence
        s2.animate().alpha(1f).setDuration(400).withEndAction(() -> {
            s3.animate().alpha(1f).setDuration(400).withEndAction(() -> {
                s4.animate().alpha(1f).setDuration(400).withEndAction(() -> {
                    s5.animate().alpha(1f).setDuration(400).withEndAction(() -> {
                        s1.animate().alpha(1f).setDuration(400).withEndAction(() -> {
                            glow.animate().alpha(1f).setDuration(400);
                            title.animate().alpha(1f).setDuration(800).withEndAction(() -> {

                                // DECISION LOGIC: Where do we go?
                                new Handler().postDelayed(() -> {
                                    SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);
                                    String existingName = prefs.getString("userName", "");

                                    Intent intent;
                                    if (existingName.isEmpty()) {
                                        // Brand new user? Take them to the landing page
                                        intent = new Intent(SplashActivity.this, MainActivity.class);
                                    } else {
                                        // Returning user? Send them right into their active Dashboard workspace!
                                        intent = new Intent(SplashActivity.this, DashboardActivity.class);
                                    }
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }, 1000);

                            });
                        });});});});});
    }
}