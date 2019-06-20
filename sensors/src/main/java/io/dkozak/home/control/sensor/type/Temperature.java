package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorClass;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Temperature extends Sensor {

    public Temperature(int identifier, int value, String description) {
        super(SensorClass.Temperature, identifier, description);
        this.setValue(value);
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
