package com.example.weatherwise.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weatherwise.R;
import com.example.weatherwise.viewmodels.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TemperatureReminderWorker extends Worker {

    private static final String CHANNEL_ID = "WeatherWiseReminderChannel";

    public TemperatureReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        double currentTemperature = getInputData().getDouble("temperature", 0.0);

        if (currentTemperature > 29) {
            sendTemperatureNotification(currentTemperature);
            addNotificationToFirestore(currentTemperature);
        }

        return Result.success();
    }


    private void sendTemperatureNotification(double temperature) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.weatherwise_logo)
                .setContentTitle("Temperature Alert")
                .setContentText("The current temperature is " + temperature + "°C, stay safe!")
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.light_salmon))
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

    private void addNotificationToFirestore(double temp) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("message", "Temperature Alert");
        notificationData.put("timestamp", System.currentTimeMillis());
        notificationData.put("userId", userId);

        firestore.collection("notifications").add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.i("TemperatureReminderWorker", "Added new notification");
                })
                .addOnFailureListener(e -> {
                    Log.e("TemperatureReminderWorker", "Failed to add new notification");
                });
    }
}
