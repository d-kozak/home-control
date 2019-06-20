package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.dkozak.home.control.sensor.rule.Rule;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.dkozak.home.control.server.firebase.DatabaseUtils.childAdded;
import static io.dkozak.home.control.server.firebase.DatabaseUtils.loadAndUpdate;
import static io.dkozak.home.control.utils.Streams.streamOf;

@Log
public class RuleEngine {
    private FirebaseDatabase database;

    private FCMMessaging messaging;

    private Map<Rule, ChildEventListener> listeners = new HashMap<>();

    public RuleEngine(FirebaseDatabase database, FCMMessaging messaging) {
        this.database = database;
        this.messaging = messaging;
        initListeners();
    }

    private void initListeners() {
        DatabaseReference ruleRef = database.getReference("rule");
        loadAndUpdate(ruleRef, snapshot -> {
            var rules = streamOf(snapshot.getChildren())
                    .map(it -> it.getValue(Rule.class))
                    .collect(Collectors.toSet());

            for (var rule : rules) {
                setupListenerFor(rule);
            }
        });

        ruleRef.addChildEventListener(new ChildChangedListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                var rule = snapshot.getValue(Rule.class);
                setupListenerFor(rule);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                var rule = snapshot.getValue(Rule.class);
                listeners.remove(rule);
            }
        });
    }

    private void setupListenerFor(Rule rule) {
        var ruleListener = database.getReference("sensor/" + rule.getSensorId() + "/values")
                                   .addChildEventListener(childAdded(snapshot -> checkRule(rule, snapshot)));
        listeners.put(rule, ruleListener);
        log.info("Listener for rule " + rule + " was added");
    }

    private void checkRule(Rule rule, DataSnapshot foo) {
        boolean isTriggered = rule.isTriggered((List<Integer>) foo.getValue(List.class));
        log.info("Rule " + rule + (isTriggered ? " IS" : " ISN'T") + " triggered");
        if (isTriggered) {
            messaging.sendMessage(rule);
        }
    }
}
