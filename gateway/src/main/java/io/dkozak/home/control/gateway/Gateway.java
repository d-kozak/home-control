package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.utils.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Gateway {

    public static void main(String[] args) {
        Log.message("Starting client");
        var sensors = DefaultSensors.get();
        connectToServer(sensors);
    }

    static void connectToServer(List<Sensor> sensors) {
        Log.message("Starting connection to server");

        Client.startCommunication(new CopyOnWriteArrayList<>(sensors));
    }
}
