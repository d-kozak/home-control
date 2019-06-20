package io.dkozak.home.control.sensor;


import io.dkozak.home.control.sensor.firebase.SensorUpdateRequest;
import io.dkozak.home.control.sensor.type.*;
import org.junit.jupiter.api.Test;

import static io.dkozak.home.control.utils.ListUtils.listOf;
import static org.assertj.core.api.Assertions.assertThat;

class SensorParserTest {

    @Test
    void getSensorTypeTest() {
        var sensors = listOf(
                new Blinder(0, 36, "0"),
                new Door(2, true, "2"),
                new HVAC(1, true, 10, "1"),
                new Light(3, false, "3"),
                new Temperature(4, 10, "4")
        );

        for (var sensor : sensors) {
            var serialized = SensorParser.serialize(sensor);
            var parsed = SensorParser.parseData(serialized);
            assertThat(sensor).isEqualTo(parsed);
        }
    }

    @Test
    void parseUpdateRequestTest() {
        assertThat(SensorParser.parseUpdateRequest("011"))
                .isEqualTo(new SensorUpdateRequest(null, 1, true));
        assertThat(SensorParser.parseUpdateRequest("070"))
                .isEqualTo(new SensorUpdateRequest(null, 7, false));
        assertThat(SensorParser.parseUpdateRequest("720"))
                .isEqualTo(new SensorUpdateRequest(null, 72, false));
        assertThat(SensorParser.parseUpdateRequest("951"))
                .isEqualTo(new SensorUpdateRequest(null, 95, true));
    }

}