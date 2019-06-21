package io.dkozak.house.control.client.view.lib;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.model.Rule;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.model.SensorUpdateRequest;
import io.dkozak.house.control.client.service.MyFirebaseMessagingService;

public abstract class SensorAwareActivity extends LoginAwareActivity {


    public static final String SENSOR_ID = "sensor_id";
    public static final String SENSOR_TYPE = "sensor-type";
    public static final String RULE_ID = "rule_id";

    public static final String RULE_PATH = "rule";
    public static final String SENSOR_TYPES_PATH = "sensor-types";
    public static final String SENSOR_PATH = "sensor";
    private ValueEventListener sensorTypeListener;
    private ValueEventListener sensorListener;
    private ValueEventListener ruleListener;


    private int currentSensorId = -1;
    private int currentSensorType = -1;
    private String currentRuleId = null;

    private Map<Integer, SensorType> sensorTypes;

    protected void setCurrentSensorId(int value) {
        this.currentSensorId = value;
    }

    protected void setCurrentRuleId(String id) {
        this.currentRuleId = id;
    }


    protected void setCurrentSensorType(int value) {
        this.currentSensorType = value;
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

    protected void onNewDeviceRules(List<Rule> deviceRules, SensorType sensorType) {

    }

    protected void onRuleDetails(Rule rule, SensorType sensorType) {

    }


    protected void removeSensor(final Integer sensorId, DatabaseReference.CompletionListener completionListener) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getUserSensorPath());
        ref.child(sensorId + "").removeValue(completionListener);
    }

    protected void removeRule(final String ruleId, DatabaseReference.CompletionListener completionListener) {
        FirebaseDatabase.getInstance().getReference(getSensorRulesForUserPath() + "/" + ruleId).removeValue(completionListener);
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


        ruleListener = database.getReference(getSensorRulesForUserPath())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot ruleSnapshot) {
                        if (currentSensorType == -1) {
                            Log.e("Sensor Aware", "currentSensorType not set...");
                            return;
                        }
                        database.getReference(SENSOR_TYPES_PATH + "/" + currentSensorType)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        SensorType sensorType = dataSnapshot.getValue(SensorType.class);
                                        List<Rule> deviceRules = new ArrayList<>();
                                        for (DataSnapshot child : ruleSnapshot.getChildren()) {
                                            Rule rule = child.getValue(Rule.class);
                                            rule.setId(child.getKey());
                                            if (rule.getSensorId() == currentSensorId) {
                                                deviceRules.add(rule);
                                            }

                                        }
                                        onNewDeviceRules(deviceRules, sensorType);
                                        if (currentRuleId != null) {
                                            for (Rule rule : deviceRules) {
                                                if (rule.getId().equals(currentRuleId)) {
                                                    onRuleDetails(rule, sensorType);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String getSensorRulesForUserPath() {
        String userId = getUserId();
        return "sensor/" + currentSensorId + "/" + RULE_PATH + "/" + userId;
    }

    private void getCurrentSensor(Map<Integer, Sensor> sensors) {
        Sensor currentSensor = sensors.get(currentSensorId);
        if (currentSensor != null && sensorTypes != null) {
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

    protected void getSensorType(final Rule rule, int currentSensorId) {
        FirebaseDatabase.getInstance().getReference(SENSOR_TYPES_PATH + "/" + currentSensorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SensorType sensorType = dataSnapshot.getValue(SensorType.class);
                        if (sensorType != null) {
                            onRuleDetails(rule, sensorType);
                        }
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
        if (ruleListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(RULE_PATH)
                    .removeEventListener(ruleListener);
            ruleListener = null;
        }
    }

    private String getUserSensorPath() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return "user/" + currentUser.getUid() + "/sensors";
    }

    protected void sensorUpdateRequest(Sensor currentSensor, boolean newValue, DatabaseReference.CompletionListener callback) {
        String user = FirebaseAuth.getInstance().getUid();
        SensorUpdateRequest request = new SensorUpdateRequest(user, currentSensor.getSensorId(), newValue);
        FirebaseDatabase.getInstance().getReference("request")
                .push()
                .setValue(request, callback);
    }

    protected void saveRule(Rule rule, DatabaseReference.CompletionListener callback) {
        saveDeviceIdIfNecessary();
        String path = getSensorRulesForUserPath();
        if (rule.getId() != null) {
            FirebaseDatabase.getInstance().getReference(path + "/" + rule.getId())
                    .setValue(rule, callback);
        } else {
            FirebaseDatabase.getInstance().getReference(path)
                    .push()
                    .setValue(rule, callback);
        }

    }

    private void saveDeviceIdIfNecessary() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Sensor Aware activity", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        final String token = task.getResult().getToken();
                        Log.d("Sensor Aware activity", "Loaded token " + token);
                        String uid = getUserId();
                        FirebaseDatabase.getInstance().getReference("user/" + uid + "/devices/" + token)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() == null) {
                                            // Log and toast
                                            Log.d("Sensor Aware activity", "Token " + token + " is not, persisting it");
                                            MyFirebaseMessagingService.persistToken(token);

                                        }
                                        Log.d("Sensor Aware activity", "Token " + token + " is already known");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
    }

    protected String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }
}
