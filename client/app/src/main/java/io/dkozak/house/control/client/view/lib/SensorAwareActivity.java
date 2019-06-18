package io.dkozak.house.control.client.view.lib;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private ValueEventListener userSensorsListener;
    private ValueEventListener sensorValuesListener;


    private Map<Integer, Sensor> sensors = new HashMap<>();

    private int sensorId;


    protected void onNewSensorTypes(Map<Integer, SensorType> sensorTypes) {

    }

    protected void onNewUserSensors(List<Sensor> sensors) {

    }

    protected void onNewSensorValues(List<List<Integer>> values) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Intent intent = getIntent();
        sensorId = intent.getIntExtra(SENSOR_ID, -1);

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
                        GenericTypeIndicator<List<Integer>> typeIndicator = new GenericTypeIndicator<List<Integer>>() {
                        };
                        List<Sensor> userSensors = new ArrayList<>();
                        List<Integer> values = dataSnapshot.getValue(typeIndicator);
                        if (values != null) {
                            for (Integer sensorId : values) {
                                Sensor sensor = sensors.get(sensorId);
                                if (sensor != null) {
                                    userSensors.add(sensor);
                                }
                            }
                            onNewUserSensors(userSensors);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (sensorId != -1) {
            sensorValuesListener = FirebaseDatabase.getInstance().getReference("sensor/" + sensorId + "/values")
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
            FirebaseDatabase.getInstance().getReference("sensor/" + sensorId + "/values")
                    .removeEventListener(sensorValuesListener);
            sensorValuesListener = null;
        }
    }

    private String getUserSensorPath() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return "user/" + currentUser.getUid() + "/sensors";
    }
}
