package io.dkozak.house.control.client.service;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.dkozak.house.control.client.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static void persistToken(final String token) {
        String id = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("user/" + id + "/devices/" + token)
                .setValue("", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null)
                            Log.d("FCM", "Token " + token + " was sent to the server");
                        else Log.d("FCM", "Sending token failed");
                    }
                });
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.d("FCM", data.toString());
        int sensorId = Integer.parseInt(data.get("Sensor ID"));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Sensor id " + sensorId)
                        .setContentText(data.get("msg"));

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(sensorId, mBuilder.build());
        } else {
            Log.e("FCM", "Could not get notification manager");
        }
    }

    @Override
    public void onNewToken(final String token) {
        persistToken(token);
    }
}
