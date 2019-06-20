package io.dkozak.house.control.client.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

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
    public void onNewToken(final String token) {
        persistToken(token);
    }
}
