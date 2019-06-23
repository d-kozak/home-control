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
        assertThat(SensorParser.parseUpdateRequest("01142"))
                .isEqualTo(new SensorUpdateRequest(null, 1, 1, 42));
        assertThat(SensorParser.parseUpdateRequest("076100"))
                .isEqualTo(new SensorUpdateRequest(null, 7, 6, 100));
        assertThat(SensorParser.parseUpdateRequest("7200"))
                .isEqualTo(new SensorUpdateRequest(null, 72, 0, 0));
        assertThat(SensorParser.parseUpdateRequest("95254"))
                .isEqualTo(new SensorUpdateRequest(null, 95, 2, 54));
    }


    @Test
    void serializeAndParseTest() {
        var updates = listOf(new SensorUpdateRequest(null, 1, 1, 42),
                new SensorUpdateRequest(null, 7, 6, 100),
                new SensorUpdateRequest(null, 72, 0, 0),
                new SensorUpdateRequest(null, 95, 2, 54));
        for (var update : updates) {
            String serialized = SensorParser.serializeSensorUpdate(update);
            assertThat(SensorParser.parseUpdateRequest(serialized))
                    .isEqualTo(update);
        }
    }
}