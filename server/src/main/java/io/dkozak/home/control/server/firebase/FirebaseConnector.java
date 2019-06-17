package io.dkozak.home.control.server.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import io.dkozak.home.control.sensor.SensorType;
import io.dkozak.home.control.server.ServerConfig;
import io.dkozak.home.control.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static io.dkozak.home.control.utils.Streams.streamOf;

public class FirebaseConnector {

    private final FirebaseApp app;

    public FirebaseConnector() {
        try {
            FileInputStream stream = new FileInputStream(ServerConfig.FIREBASE_CREDENTIALS);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .setDatabaseUrl("https://houseconrol-dkozak.firebaseio.com")
                    .build();

            this.app = FirebaseApp.initializeApp(options);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public void updateSensorTypes(Set<SensorType> sensorTypes) {
        var database = FirebaseDatabase.getInstance(app);
        var executed = new AtomicReference<ValueEventListener>();
        DatabaseReference ref = database.getReference("sensor-types");
        executed.set(ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (executed.get() != null) {
                    ref.removeEventListener(executed.get());
                    executed.set(null);

                    var oldSensors = streamOf(snapshot.getChildren())
                            .map(item -> item.getValue(SensorType.class))
                            .collect(Collectors.toSet());
                    Log.message("Old sensors are " + oldSensors);


                    var allSensors = new HashSet<>(oldSensors);
                    allSensors.addAll(sensorTypes);

                    Log.message("Persisting new sensor types: " + sensorTypes);
                    var future = ref.setValueAsync(new ArrayList<>(allSensors));
                    try {
                        future.get();
                        Log.message("update done");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        Log.message("failed");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        }));


    }
}
