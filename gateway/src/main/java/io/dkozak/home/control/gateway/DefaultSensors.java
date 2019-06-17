package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.type.*;
import io.dkozak.home.control.utils.Log;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class DefaultSensors {

    static List<Sensor> get() {

        Log.message("Initializing io.dkozak.home.control.sensor list");

        var sensors = listOf(
                new Temperature(0, 1, 20, "Room 1"),
                new Temperature(0, 2, 20, "Room 2"),
                new Temperature(0, 3, 20, "Room 3"),

                new Blinder(1, 4, 0, "Room 1"),
                new Blinder(1, 5, 100, "Room 2"),
                new Blinder(1, 6, 50, "Room 3"),

                new Door(2, 7, false, "Room 1"),
                new Door(2, 8, true, "Room 2"),
                new Door(2, 9, true, "Room 3"),

                new Light(3, 10, false, "Room 1"),
                new Light(3, 11, false, "Room 2"),
                new Light(3, 12, true, "Room 3"),

                new HVAC(4, 13, false, 0, "Room 1"),
                new HVAC(4, 14, false, 0, "Room 2"),
                new HVAC(4, 15, true, 20, "Room 3")
        );

        Log.message("Sensor list: " + sensors.toString());
        return sensors;
    }
}
