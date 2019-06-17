package io.dkozak.home.control.server.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.server.ServerConfig;
import io.dkozak.home.control.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.dkozak.home.control.utils.Streams.streamOf;

public class FirebaseConnector {

    private final FirebaseApp app;
    private final FirebaseDatabase database;

    private final DatabaseReference.CompletionListener logResultListener = (error, __) -> {
        if (error == null) {
            Log.message("update done");
        } else {
            Log.message("failed");
            error.toException()
                 .printStackTrace();
        }
    };

    public FirebaseConnector() {
        try {
            FileInputStream stream = new FileInputStream(ServerConfig.FIREBASE_CREDENTIALS);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .setDatabaseUrl("https://houseconrol-dkozak.firebaseio.com")
                    .build();

            this.app = FirebaseApp.initializeApp(options);
            this.database = FirebaseDatabase.getInstance(this.app);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public <T> void updateList(Set<T> newData, Class<T> clazz, String databasePath) {
        DatabaseReference ref = database.getReference(databasePath);
        loadAndUpdate(ref, snapshot -> {
            var oldData = streamOf(snapshot.getChildren())
                    .map(item -> item.getValue(clazz))
                    .collect(Collectors.toSet());
            Log.message("Old elements are " + oldData);

            var allData = new TreeSet<>(oldData);
            allData.addAll(newData);

            Log.message("Persisting new elements: " + newData);
            ref.setValue(new ArrayList<>(allData), logResultListener);
        });
    }

    private void loadAndUpdate(DatabaseReference databaseRef, Consumer<DataSnapshot> block) {
        var listenerRef = new AtomicReference<ValueEventListener>();
        listenerRef.set(databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (listenerRef.get() != null) {
                    databaseRef.removeEventListener(listenerRef.get());
                    listenerRef.set(null);
                    block.accept(snapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        }));
    }

    public void newSensorData(Sensor sensorData) {
        DatabaseReference ref = database.getReference("sensor/" + sensorData.getIdentifier());
        loadAndUpdate(ref, snapshot -> {
            FirebaseSensor sensor = snapshot.getValue(FirebaseSensor.class);

            sensor.getValues()
                  .add(sensorData.getData());

            ref.setValue(sensor, logResultListener);
        });
    }


    public List<Integer> parseSerializedData(String serialized) {
        var result = new ArrayList<Integer>();

        for (int i = 0; i < serialized.length() - 1; i += 2) {
            result.add(Integer.parseInt(serialized.substring(i, i + 2)));
        }

        return result;
    }

    public void executeRulesFor(Sensor sensorData) {

    }
}
