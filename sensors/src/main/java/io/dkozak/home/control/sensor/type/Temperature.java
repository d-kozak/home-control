package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Temperature extends Sensor {

    public Temperature(int nSensorClass, int nIdentifier, int nValue, String szDescription) {
        super(nSensorClass, nIdentifier, szDescription);
        this.setValue(nValue);
    }

    @Override
    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass, identifier, listOf(listOf(value)));
    }

    @Override
    public List<Integer> getData() {
        return listOf(value);
    }

    public void setValue(int nValue) {

        if (nValue < -100 || nValue > 100) {
            System.out.println("Invalid value");
            return;
        }

        this.value = nValue;
    }

    public String toString() {
        String szValue = String.valueOf(value);
        if (szValue.length() == 1) {
            szValue = "0" + szValue;
        }

        return super.toString() + szValue;
    }
}
