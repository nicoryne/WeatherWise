package com.example.weatherwise.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.weatherwise.R;
import com.example.weatherwise.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HydrationReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WeatherWise")
                .setSmallIcon(R.drawable.logo_colored)
                .setContentTitle("Time to Rehydrate")
                .setContentText("It's time to drink some water!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }

        addNotificationToFirestore();
    }

    private void addNotificationToFirestore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("message", "It's time to drink some water!");
        notificationData.put("timestamp", System.currentTimeMillis());

        firestore.collection("notifications")
                .add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d("HydrationReminder", "Notification added to Firestore"))
                .addOnFailureListener(e -> Log.e("HydrationReminder", "Error adding notification to Firestore", e));
    }
}
