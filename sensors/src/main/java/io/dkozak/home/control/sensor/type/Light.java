package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.sensor.firebase.SensorValue;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Light extends Sensor {

    public Light(int sensorClass, int identifier, boolean bState, String description) {
        super(sensorClass, identifier, description);
        this.setIsOn(bState);
    }

    @Override
    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass, identifier, listOf(new SensorValue(listOf(value))));
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
