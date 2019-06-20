package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.type.*;
import lombok.extern.java.Log;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

@Log
public class DefaultSensors {

    static List<Sensor> get() {

        log.info("Initializing io.dkozak.home.control.sensor list");

        var sensors = listOf(
                new Temperature(0, 20, "Room 1"),
                new Temperature(1, 20, "Room 2"),
                new Temperature(2, 20, "Room 3"),

                new Blinder(3, 0, "Room 1"),
                new Blinder(4, 100, "Room 2"),
                new Blinder(5, 50, "Room 3"),

                new Door(6, false, "Room 1"),
                new Door(7, true, "Room 2"),
                new Door(8, true, "Room 3"),

                new Light(9, false, "Room 1"),
                new Light(10, false, "Room 2"),
                new Light(11, true, "Room 3"),

                new HVAC(12, false, 0, "Room 1"),
                new HVAC(13, false, 0, "Room 2"),
                new HVAC(14, true, 20, "Room 3")
        );

        log.info("Sensor list: " + sensors.toString());
        return sensors;
    }
}
