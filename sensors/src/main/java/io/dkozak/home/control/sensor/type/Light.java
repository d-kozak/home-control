package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorClass;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Light extends Sensor {

    public Light(int identifier, boolean bState, String description) {
        super(SensorClass.Light, identifier, description);
        this.setIsOn(bState);
    }

    @Override
    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass.ordinal(), identifier, listOf(listOf(value)));
    }

    @Override
    public List<Integer> getData() {
        return listOf(value);
    }

    public boolean isOn() {
        return this.value != 0;
    }

    public void setIsOn(boolean newState) {
        this.value = newState ? 1 : 0;
    }

    public String toString() {
        return super.toString() + (isOn() ? "01" : "00");
    }
}
