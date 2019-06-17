package io.dkozak.home.control.sensor;


import org.junit.jupiter.api.Test;

import static io.dkozak.home.control.utils.ListUtils.listOf;
import static io.dkozak.home.control.utils.Pair.pairOf;

class SensorTest {


    @Test
    void getSensorTypeTest() {
        var sensorType = new Blinder(1, 1, 1, "foo").getSensorType();
        assertThat(sensorType)
                .isEqualTo(new SensorType(1, "Blinder"));

        var sensors = listOf(
                pairOf(new HVAC(1, 1, true, 10, ""), "HVAC"),
                pairOf(new Door(2, 1, true, ""), "Door"),
                pairOf(new Light(3, 1, true, ""), "Light")
        );

        for (var pair : sensors) {
            var sensor = pair.first;
            var name = pair.second;

            assertThat(sensor.getSensorType())
                    .isEqualTo(new SensorType(sensor.getSensorClass(), name));
        }

    }
}