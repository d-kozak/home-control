package io.dkozak.home.control.sensor;

import io.dkozak.home.control.sensor.type.Blinder;
import io.dkozak.home.control.sensor.type.Door;
import org.junit.jupiter.api.Test;

import static io.dkozak.home.control.utils.ListUtils.listOf;
import static org.assertj.core.api.Assertions.assertThat;

public class SensorProcessorTest {

    @Test
    public void generateRandomDataTest__door() {
        var door = new Door(1, true, "");

        var rnd = SensorProcessor.generateRandomData(listOf(door));
        var parsed = SensorParser.parseData(rnd);
        assertThat(parsed.getSensorClass())
                .isEqualTo(SensorClass.Door);
        var values = parsed.getData();
        assertThat(values).hasSize(1);
        assertThat(values.get(0))
                .isGreaterThanOrEqualTo(0)
                .isLessThan(2);
    }

    @Test
    public void generateRandomDataTest__blinder() {
        var door = new Blinder(1, 75, "");

        var rnd = SensorProcessor.generateRandomData(listOf(door));
        var parsed = SensorParser.parseData(rnd);
        assertThat(parsed.getSensorClass())
                .isEqualTo(SensorClass.Blinder);
        var values = parsed.getData();
        assertThat(values).hasSize(1);
        assertThat(values.get(0))
                .isGreaterThanOrEqualTo(0)
                .isLessThanOrEqualTo(100);
    }

}
