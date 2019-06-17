package io.dkozak.home.control.server.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.dkozak.home.control.sensor.rule.Rule;
import io.dkozak.home.control.utils.Log;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FCMMessaging {

    private final FirebaseMessaging messaging;

    public void sendMessage(Rule rule) {
        try {
            var token = rule.getDeviceId();
            var message = Message.builder()
                                 .putData("Title", "Event")
                                 .putData("Sensor ID", rule.sensorId + "")
                                 .setToken(token)
                                 .build();
            Log.message("Sending message: " + message);
            var response = messaging.send(message);
            Log.message("Received response: " + response);
        } catch (FirebaseMessagingException ex) {
            throw new RuntimeException(ex);
        }

    }
}
