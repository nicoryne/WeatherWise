package com.example.weatherwise.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weatherwise.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HydrationReminderWorker extends Worker {

    private static final String CHANNEL_ID = "WeatherWiseReminderChannel";

    public HydrationReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Send notification
        sendNotification();

        // Add notification to Firestore
        addNotificationToFirestore();

        return Result.success();
    }

    private void sendNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.water)
                .setContentTitle("Time to Rehydrate")
                .setContentText("It's time to drink water!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = "WeatherWise Reminder Channel";
        String description = "Channel for WeatherWise reminders";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void addNotificationToFirestore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", "It's time to drink water!");
        notificationData.put("timestamp", System.currentTimeMillis());
        notificationData.put("userId", userId);

        firestore.collection("notifications").add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.i("HydrationReminderWorker", "Added new notification");
                })
                .addOnFailureListener(e -> {
                    Log.e("HydrationReminderWorker", "Failed to add new notification");
                });
    }
}
