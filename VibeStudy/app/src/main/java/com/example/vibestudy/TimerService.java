package com.example.vibestudy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    public static final String CHANNEL_ID = "CosmicTimerChannel";
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeLeftInMillis = intent.getLongExtra("time_millis", 1500000);

        // Lock the service into the phone status bar immediately
        startForeground(1, buildNotification("Syncing cosmic countdown...", false));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;

                int hours = (int) (timeLeftInMillis / 1000) / 3600;
                int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.notify(1, buildNotification("Time Remaining: " + timeStr, false));
                }
            }

            @Override
            public void onFinish() {
                SharedPreferences prefs = getSharedPreferences("StudyVibesPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("savedTimeLeft", 0);
                editor.putBoolean("savedTimerRunning", false);
                editor.putBoolean("showTimerFinishedDialog", true);
                editor.apply();

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.cancel(1); // Wipe out active tracking numbers
                    // DROP DOWN BANNER PING: Pushes completion alert card visually from top of phone screen
                    nm.notify(2, buildNotification("Focus window complete! Time to take a break.", true));
                }

                stopSelf();
            }
        }.start();

        return START_NOT_STICKY;
    }

    private Notification buildNotification(String text, boolean isFinished) {
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(isFinished ? "Session Finished! ✨" : "Cosmic Focus Session Running")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setAutoCancel(isFinished)
                .setOnlyAlertOnce(!isFinished)
                .setOngoing(!isFinished);

        if (isFinished) {
            // Priority High combined with default system sounds forces Android to execute a Heads-Up drop down alert
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{0, 500, 250, 500})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}