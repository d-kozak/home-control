package io.dkozak.home.control.sensor;

import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.dkozak.home.control.utils.ListUtils.listOf;

@Data
public abstract class Sensor {

    protected SensorClass sensorClass;
    protected int identifier;
    protected int value = 0;
    protected String description;


    public Sensor(SensorClass sensorClass, int identifier, String description) {
        this.sensorClass = sensorClass;
        this.identifier = identifier;
        this.description = description;
    }

    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass.ordinal(), identifier, description, listOf(getData()), Collections.emptyMap());
    }

    public abstract List<Integer> getData();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        Sensor sensor = (Sensor) o;
        return sensorClass == sensor.sensorClass &&
                identifier == sensor.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorClass, identifier);
    }

    public String toString() {
        String szSensorClass = Integer.toString(sensorClass.ordinal());
        if (szSensorClass.length() == 1) {
            szSensorClass = "0" + szSensorClass;
        }

        String szSensorIdentifier = String.valueOf(identifier);
        if (szSensorIdentifier.length() == 1) {
            szSensorIdentifier = "0" + szSensorIdentifier;
        }

        return szSensorClass + szSensorIdentifier;
    }
}
