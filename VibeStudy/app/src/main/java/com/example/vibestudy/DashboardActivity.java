package com.example.vibestudy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView timerDisplay;
    private ImageButton btnTimerControl;
    private LinearLayout tasksContainer;

    private CountDownTimer localTimer;
    private long totalTimeLeftInMillis = 1500000; // 25 Mins Default
    private boolean isTimerRunning = false;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.dashboard_greeting);
        timerDisplay = findViewById(R.id.tv_timer_display);
        btnTimerControl = findViewById(R.id.btn_timer_control);
        tasksContainer = findViewById(R.id.dynamic_tasks_container);

        ImageButton btnOpenSettings = findViewById(R.id.btn_open_settings);
        Button btnAddTask = findViewById(R.id.btn_add_task);

        // FORCE REGISTRATION: Channel engine maps into system before anything spins up
        createSystemNotificationChannel();
        checkNotificationPermissions();

        if (btnOpenSettings != null) {
            btnOpenSettings.setColorFilter(Color.parseColor("#A7FFEB"), PorterDuff.Mode.SRC_IN);
            btnOpenSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsMenuActivity.class)));
        }

        btnTimerControl.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseLocalTimer();
            } else {
                startLocalTimer();
            }
        });

        timerDisplay.setOnClickListener(v -> {
            if (isTimerRunning) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Slide to Adjust Duration");

            LinearLayout dialogLayout = new LinearLayout(this);
            dialogLayout.setOrientation(LinearLayout.HORIZONTAL);
            dialogLayout.setGravity(Gravity.CENTER);

            final NumberPicker hourPicker = new NumberPicker(this);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setValue((int) (totalTimeLeftInMillis / 3600000));

            final NumberPicker minutePicker = new NumberPicker(this);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue((int) ((totalTimeLeftInMillis % 3600000) / 60000));

            final NumberPicker secondPicker = new NumberPicker(this);
            secondPicker.setMinValue(0);
            secondPicker.setMaxValue(59);
            secondPicker.setValue((int) ((totalTimeLeftInMillis % 60000) / 1000));

            dialogLayout.addView(hourPicker);
            dialogLayout.addView(minutePicker);
            dialogLayout.addView(secondPicker);
            builder.setView(dialogLayout);

            builder.setPositiveButton("Set Time", (dialog, which) -> {
                long selectedHours = hourPicker.getValue();
                long selectedMinutes = minutePicker.getValue();
                long selectedSeconds = secondPicker.getValue();

                totalTimeLeftInMillis = ((selectedHours * 3600) + (selectedMinutes * 60) + selectedSeconds) * 1000;
                updateTimerTextDisplay();
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        btnAddTask.setOnClickListener(v -> {
            final LinearLayout rowLayout = new LinearLayout(DashboardActivity.this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 6, 0, 6);
            rowLayout.setLayoutParams(rowParams);

            EditText taskField = new EditText(DashboardActivity.this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            taskField.setLayoutParams(textParams);
            taskField.setHint("• Enter study objective...");
            taskField.setHintTextColor(Color.parseColor("#60F0FFF0"));
            taskField.setTextColor(Color.parseColor("#F0FFF0"));
            taskField.setBackground(null);
            taskField.setTextSize(15);
            taskField.setPadding(8, 12, 8, 12);
            taskField.setSingleLine(true);

            ImageButton btnDelete = new ImageButton(DashboardActivity.this);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(40, 40);
            btnParams.setMargins(8, 0, 8, 0);
            btnDelete.setLayoutParams(btnParams);
            btnDelete.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnDelete.setColorFilter(Color.parseColor("#FF8A80"), PorterDuff.Mode.SRC_IN);
            btnDelete.setBackground(null);

            btnDelete.setOnClickListener(view -> {
                tasksContainer.removeView(rowLayout);
                Toast.makeText(DashboardActivity.this, "Objective cleared!", Toast.LENGTH_SHORT).show();
            });

            rowLayout.addView(taskField);
            rowLayout.addView(btnDelete);
            tasksContainer.addView(rowLayout);

            taskField.requestFocus();
        });
    }

    private void createSystemNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Setting this to IMPORTANCE_HIGH inside Activity startup configures drop-downs correctly on launch
            NotificationChannel channel = new NotificationChannel(
                    TimerService.CHANNEL_ID, "Focus Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Background focus countdown channel updates");
            channel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showTimerFinishedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Complete! ✨");
        builder.setMessage("Your cosmic study countdown has ended. Outstanding work staying focused!");
        builder.setPositiveButton("Awesome", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }

    private void startLocalTimer() {
        if (localTimer != null) localTimer.cancel();

        localTimer = new CountDownTimer(totalTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                totalTimeLeftInMillis = millisUntilFinished;
                updateTimerTextDisplay();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                totalTimeLeftInMillis = 0;
                updateTimerTextDisplay();
                btnTimerControl.setImageResource(android.R.drawable.ic_media_play);
                showTimerFinishedDialog();
            }
        }.start();

        isTimerRunning = true;
        btnTimerControl.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pauseLocalTimer() {
        if (localTimer != null) localTimer.cancel();
        isTimerRunning = false;
        btnTimerControl.setImageResource(android.R.drawable.ic_media_play);
    }

    private void updateTimerTextDisplay() {
        int hours = (int) (totalTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((totalTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (totalTimeLeftInMillis / 1000) % 60;
        timerDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void checkNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Instantly kill the background service when returning to prevent thread collisions
        stopService(new Intent(this, TimerService.class));

        SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);

        // 1. Check if the timer reached zero while the app was closed
        if (prefs.getBoolean("showTimerFinishedDialog", false)) {
            showTimerFinishedDialog();

            // Wipe the alert flag so it doesn't loop trigger
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("showTimerFinishedDialog", false);

            // FORCE STATE RESET: Clear out old time states to prevent visual freezes
            totalTimeLeftInMillis = 0;
            isTimerRunning = false;
            editor.putLong("savedTimeLeft", 0);
            editor.putBoolean("savedTimerRunning", false);
            editor.apply();

            btnTimerControl.setImageResource(android.R.drawable.ic_media_play);
            updateTimerTextDisplay();
            welcomeText.setText("Greetings from the stars, " + prefs.getString("userName", "Explorer") + "!");
            return; // Safe early exit for completed background sessions
        }

        // 2. Load standard application memory loops
        boolean wasRunning = prefs.getBoolean("savedTimerRunning", false);
        if (wasRunning) {
            long endTimeStamp = prefs.getLong("savedEndTimeStamp", 0);
            totalTimeLeftInMillis = endTimeStamp - System.currentTimeMillis();

            if (totalTimeLeftInMillis <= 0) {
                totalTimeLeftInMillis = 0;
                isTimerRunning = false;
                btnTimerControl.setImageResource(android.R.drawable.ic_media_play);
                showTimerFinishedDialog();
            } else {
                startLocalTimer(); // Sync ongoing ticking loop
            }
        } else {
            // FIX: Pull the exact user-scrolled duration from storage instead of forcing 25 mins
            totalTimeLeftInMillis = prefs.getLong("savedTimeLeft", 1500000);
            isTimerRunning = false;
            btnTimerControl.setImageResource(android.R.drawable.ic_media_play);
        }

        updateTimerTextDisplay();
        welcomeText.setText("Greetings from the stars, " + prefs.getString("userName", "Explorer") + "!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        long currentEndTimeStamp = System.currentTimeMillis() + totalTimeLeftInMillis;

        // Save the current numbers flawlessly into local persistent memory
        editor.putLong("savedTimeLeft", totalTimeLeftInMillis);
        editor.putBoolean("savedTimerRunning", isTimerRunning);
        editor.putLong("savedEndTimeStamp", currentEndTimeStamp);
        editor.apply();

        // If the workspace countdown is active, offload it onto the system tray service banner
        if (isTimerRunning) {
            if (localTimer != null) localTimer.cancel();
            Intent serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("time_millis", totalTimeLeftInMillis);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }
}