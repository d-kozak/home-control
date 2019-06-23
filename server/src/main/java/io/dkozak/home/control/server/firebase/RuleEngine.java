package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.SensorType;
import io.dkozak.home.control.sensor.rule.Rule;
import lombok.extern.java.Log;

import java.util.List;

@Log
public class RuleEngine {
    private FirebaseDatabase database;

    private FCMMessaging messaging;


    public RuleEngine(FirebaseDatabase database, FCMMessaging messaging) {
        this.database = database;
        this.messaging = messaging;
    }


    public void newValuesFor(Sensor sensor) {
        log.info("checking sensor " + sensor);

        FirebaseDatabase.getInstance()
                        .getReference("sensor-types/" + sensor.getSensorClass()
                                                              .ordinal())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                SensorType sensorType = snapshot.getValue(SensorType.class);
                                log.info("Loaded sensor type " + sensorType);
                                FirebaseDatabase.getInstance()
                                                .getReference("sensor/" + sensor.getIdentifier() + "/rule")
                                                .addListenerForSingleValueEvent(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot snapshot) {
                                                                log.info("rules loaded, started checking...");
                                                                for (var userRules : snapshot.getChildren()) {
                                                                    for (var rule : userRules.getChildren()) {
                                                                        var parsedRule = rule.getValue(Rule.class);
                                                                        if (parsedRule != null) {
                                                                            checkRule(parsedRule, sensorType, sensor.getData());
                                                                        } else {
                                                                            log.severe("Could not parse rule at " + parsedRule);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {

                                                            }
                                                        }
                                                );
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });


    }

    private void checkRule(Rule rule, SensorType sensorType, List<Integer> newValues) {
        boolean isTriggered = rule.isTriggered(newValues);
        log.info("Rule " + rule + (isTriggered ? " IS" : " ISN'T") + " triggered");
        if (isTriggered) {
            messaging.sendMessage(rule, sensorType);
        }
    }
}
