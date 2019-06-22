package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import lombok.extern.java.Log;
import lombok.var;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Log
public class Gateway {

    public static void main(String[] args) {
        log.info("Starting client");
        var sensors = DefaultSensors.get();
        connectToServer(sensors);
    }

    static void connectToServer(List<Sensor> sensors) {
        log.info("Starting connection to server");

        Client.startCommunication(new CopyOnWriteArrayList<>(sensors));
    }
}
