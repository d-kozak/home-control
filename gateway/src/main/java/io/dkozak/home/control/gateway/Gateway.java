package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.*;
import io.dkozak.home.control.utils.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Gateway {

    public static void main(String[] args) {
        Log.message("Starting client");

        var sensors = initSensors();
        connectToServer(sensors);
    }


    static List<Sensor> initSensors() {

        Log.message("Initializing io.dkozak.home.control.sensor list");

        var sensors = listOf(
                new Temperature(0, 1, 20, "Room 1"),
                new Temperature(0, 2, 20, "Room 2"),
                new Temperature(0, 3, 20, "Room 3"),

                new Blinder(1, 1, 0, "Room 1"),
                new Blinder(1, 2, 100, "Room 2"),
                new Blinder(1, 3, 50, "Room 3"),

                new Door(2, 1, false, "Room 1"),
                new Door(2, 2, true, "Room 2"),
                new Door(2, 3, true, "Room 3"),

                new Light(3, 1, false, "Room 1"),
                new Light(3, 2, false, "Room 2"),
                new Light(3, 3, true, "Room 3"),

                new HVAC(4, 1, false, 0, "Room 1"),
                new HVAC(4, 2, false, 0, "Room 2"),
                new HVAC(4, 3, true, 20, "Room 3")
        );

        Log.message("Sensor list: " + sensors.toString());
        return sensors;
    }


    static void connectToServer(List<Sensor> sensors) {
        Log.message("Starting connection to server");

        Client.startCommunication(new CopyOnWriteArrayList<>(sensors));
    }
}
