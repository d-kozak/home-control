package io.dkozak.house.control.client.view.lib;

import android.util.Log;

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
import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public abstract class SensorAwareActivity extends LoginAwareActivity {

    public static final String SENSOR_ID = "sensor_id";

    public static final String SENSOR_TYPES_PATH = "sensor-types";
    public static final String SENSOR_PATH = "sensor";
    private ValueEventListener sensorTypeListener;
    private ValueEventListener sensorListener;
    private ValueEventListener sensorValuesListener;


    private int currentSensorId = -1;

    private Map<Integer, SensorType> sensorTypes;

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

    protected void onNewSensorValues(Sensor currentSensor, SensorType sensorType) {

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

    protected void setupDatabaseListeners() {
        clearDatabaseListeners();
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
                        SensorAwareActivity.this.sensorTypes = sensorTypes;
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
                        Map<Integer, Sensor> sensors = new HashMap<>();
                        if (allSensors != null) {
                            for (Sensor sensor : allSensors) {
                                sensors.put(sensor.getSensorId(), sensor);
                            }
                        }
                        getUserSensors(sensors);
                        if (currentSensorId != -1) {
                            getCurrentSensor(sensors);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getCurrentSensor(Map<Integer, Sensor> sensors) {
        Sensor currentSensor = sensors.get(currentSensorId);
        if (currentSensor != null) {
            SensorType sensorType = sensorTypes.get(currentSensor.getSensorType());
            if (sensorType != null) {
                onNewSensorValues(currentSensor, sensorType);
            } else {
                Log.e("Sensors", "Could not find sensor type for sensor with id " + currentSensorId);
            }
        } else {
            Log.e("Sensors", "Could not find sensor with id " + currentSensorId);
        }
    }

    private void getUserSensors(final Map<Integer, Sensor> map) {
        FirebaseDatabase.getInstance().getReference(getUserSensorPath())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Sensor> userSensors = new ArrayList<>();
                        List<Integer> userSensorIds = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Integer sensorId = child.getValue(Integer.class);
                            Sensor sensor = map.get(sensorId);
                            if (sensor != null) {
                                userSensors.add(sensor);
                                userSensorIds.add(sensorId);
                            }
                        }
                        onNewUserSensors(userSensors);
                        getNonUserSensors(map, userSensorIds);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getNonUserSensors(Map<Integer, Sensor> sensors, List<Integer> userSensorIds) {
        List<Sensor> nonUserSensors = new ArrayList<>();
        for (Map.Entry<Integer, Sensor> entry : sensors.entrySet()) {
            Integer id = entry.getKey();
            if (!userSensorIds.contains(id)) {
                nonUserSensors.add(entry.getValue());
            }
        }
        onNewNonUserSensors(nonUserSensors);
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
    }

    private String getUserSensorPath() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return "user/" + currentUser.getUid() + "/sensors";
    }
}
