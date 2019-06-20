package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorClass;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Blinder extends Sensor {

    public Blinder(int identifier, int percentage, String description) {
        super(SensorClass.Blinder, identifier, description);
        this.setValue(percentage);
    }

    @Override
    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass.ordinal(), identifier, listOf(listOf(value)));
    }

    @Override
    public List<Integer> getData() {
        return listOf(value);
    }

    public void setValue(int nPercentage) {

        if (nPercentage < 0 || nPercentage > 100) {
            System.out.println("Invalid value");
            return;
        }

        this.value = nPercentage;
    }

    public String toString() {
        String szValue = String.valueOf(value);
        if (szValue.length() == 1) {
            szValue = "00" + szValue;
        } else if (szValue.length() == 2) {
            szValue = "0" + szValue;
        }

        return super.toString() + szValue;
    }
}
