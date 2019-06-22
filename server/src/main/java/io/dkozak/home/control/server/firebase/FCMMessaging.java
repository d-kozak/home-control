package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.dkozak.home.control.sensor.rule.Rule;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@AllArgsConstructor
public class FCMMessaging {

    private final FirebaseMessaging messaging;

    public void sendMessage(Rule rule) {
        log.info("preparing to send notifications to " + rule.getUserId());
        FirebaseDatabase.getInstance()
                        .getReference("user/" + rule.getUserId() + "/devices")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                log.info("starting sending messages");
                                for (var device : snapshot.getChildren()) {
                                    log.info("sending message to " + device);
                                    sendMessageToDevice(rule, device.getKey());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
    }

    private void sendMessageToDevice(Rule rule, String token) {
        try {
            var message = Message.builder()
                                 .putData("Title", "Event")
                                 .putData("Sensor ID", rule.getSensorId() + "")
                                 .setToken(token)
                                 .build();
            log.info("Sending message: " + message);
            var response = messaging.send(message);
            log.info("Received response: " + response);
        } catch (FirebaseMessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
