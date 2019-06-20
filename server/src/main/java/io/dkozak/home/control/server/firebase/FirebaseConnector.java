package io.dkozak.home.control.server.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.sensor.firebase.SensorUpdateRequest;
import io.dkozak.home.control.server.ServerConfig;
import lombok.extern.java.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.dkozak.home.control.server.firebase.DatabaseUtils.loadAndUpdate;
import static io.dkozak.home.control.utils.Streams.streamOf;

@Log
public class FirebaseConnector {

    private final FirebaseApp app;
    private final FirebaseDatabase database;
    private final RuleEngine ruleEngine;

    private final DatabaseReference.CompletionListener logResultListener = (error, __) -> {
        if (error == null) {
            log.info("update done");
        } else {
            log.info("failed");
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
            this.ruleEngine = new RuleEngine(this.database, new FCMMessaging(FirebaseMessaging.getInstance(this.app)));
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
            log.info("Old elements are " + oldData);

            var allData = new TreeSet<>(oldData);
            allData.addAll(newData);

            log.info("Persisting new elements: " + newData);
            ref.setValue(new ArrayList<>(allData), logResultListener);
        });
    }


    public void onUserRequestedSensorUpdate(Consumer<SensorUpdateRequest> callback) {
        database.getReference("/request")
                .addChildEventListener(new ChildAddedListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        var request = snapshot.getValue(SensorUpdateRequest.class);

                        database.getReference("user/" + request.getUser() + "/sensors")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        var typeIndicator = new GenericTypeIndicator<List<Integer>>() {
                                        };
                                        var userSensors = snapshot.getValue(typeIndicator);
                                        if (userSensors.contains(request.getSensorId())) {
                                            callback.accept(request);
                                        } else {
                                            log.severe("User " + request.getUser() + " tried to update a sensor, which is not his");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });
                    }
                });
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
}
