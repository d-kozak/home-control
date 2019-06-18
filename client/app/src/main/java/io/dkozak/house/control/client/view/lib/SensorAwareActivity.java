package io.dkozak.house.control.client.view.lib;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public abstract class SensorAwareActivity extends LoginAwareActivity {

    public static final String SENSOR_ID = "sensor_id";

    public static final String SENSOR_TYPES_PATH = "sensor-types";
    public static final String SENSOR_PATH = "sensor";
    private ValueEventListener sensorTypeListener;
    private ValueEventListener sensorListener;
    private ValueEventListener userSensorsListener;
    private ValueEventListener sensorValuesListener;


    private Map<Integer, Sensor> sensors = new HashMap<>();

    private int currentSensorId = -1;

    protected void setCurrentSensorId(int value) {
        this.currentSensorId = value;
    }

    protected void onNewSensorTypes(Map<Integer, SensorType> sensorTypes) {

    }

    protected void onNewAllSensors(List<Sensor> sensors) {

    }

    protected void onNewUserSensors(List<Sensor> userSensors) {

    }

    protected void onNewNonUserSensors(List<Sensor> sensors) {

    }

    protected void onNewSensorValues(List<List<Integer>> values) {

    }


    protected void removeSensor(final Integer sensorId, DatabaseReference.CompletionListener completionListener) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getUserSensorPath());
        ref.child(sensorId + "").removeValue(completionListener);
    }

    protected void addNewSensor(final Sensor sensor, final DatabaseReference.CompletionListener onComplete) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getUserSensorPath());
        ref.child(sensor.getSensorId() + "").setValue(sensor.getSensorId(), onComplete);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDatabaseListeners();
    }

    private void setupDatabaseListeners() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        sensorTypeListener = database.getReference(SENSOR_TYPES_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<Integer, SensorType> sensorTypes = new HashMap<>();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            sensorTypes.put(Integer.parseInt(item.getKey()), item.getValue(SensorType.class));
                        }
                        onNewSensorTypes(sensorTypes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        sensorListener = database.getReference(SENSOR_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<Sensor>> typeIndicator = new GenericTypeIndicator<List<Sensor>>() {
                        };
                        List<Sensor> allSensors = dataSnapshot.getValue(typeIndicator);
                        onNewAllSensors(allSensors);
                        Map<Integer, Sensor> map = new HashMap<>();
                        if (allSensors != null) {
                            for (Sensor sensor : allSensors) {
                                map.put(sensor.getSensorId(), sensor);
                            }
                        }
                        sensors = map;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        userSensorsListener = database.getReference(getUserSensorPath())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Sensor> userSensors = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Integer sensorId = child.getValue(Integer.class);
                            Sensor sensor = sensors.get(sensorId);
                            if (sensor != null) {
                                userSensors.add(sensor);
                            }
                        }
                        onNewUserSensors(userSensors);
                        getNonUserSensors(new HashSet<>(userSensors));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (currentSensorId != -1) {
            sensorValuesListener = FirebaseDatabase.getInstance().getReference("sensor/" + currentSensorId + "/values")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<List<List<Integer>>> typeIndicator = new GenericTypeIndicator<List<List<Integer>>>() {
                            };
                            List<List<Integer>> values = dataSnapshot.getValue(typeIndicator);
                            if (values != null) {
                                onNewSensorValues(values);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void getNonUserSensors(final Set<Sensor> userSensors) {
        FirebaseDatabase.getInstance().getReference(SENSOR_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<Sensor>> typeIndicator = new GenericTypeIndicator<List<Sensor>>() {
                        };
                        List<Sensor> allSensors = dataSnapshot.getValue(typeIndicator);
                        List<Sensor> filtered = new ArrayList<>();
                        if (allSensors != null) {
                            for (Sensor sensor : allSensors) {
                                if (!userSensors.contains(sensor)) {
                                    filtered.add(sensor);
                                }
                            }
                        }
                        onNewNonUserSensors(filtered);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDatabaseListeners();
    }

    private void clearDatabaseListeners() {
        if (sensorTypeListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(SENSOR_TYPES_PATH)
                    .removeEventListener(sensorTypeListener);
            sensorTypeListener = null;
        }
        if (sensorListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(SENSOR_PATH)
                    .removeEventListener(sensorListener);
            sensorListener = null;
        }
        if (userSensorsListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(getUserSensorPath())
                    .removeEventListener(userSensorsListener);
            userSensorsListener = null;
        }
        if (sensorValuesListener != null) {
            FirebaseDatabase.getInstance().getReference("sensor/" + currentSensorId + "/values")
                    .removeEventListener(sensorValuesListener);
            sensorValuesListener = null;
        }
    }

    private String getUserSensorPath() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return "user/" + currentUser.getUid() + "/sensors";
    }
}
