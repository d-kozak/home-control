package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.sensor.firebase.SensorValue;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class HVAC extends Sensor {

    private boolean bState = false;

    public HVAC(int sensorClass, int identifier, boolean bState, int temperature, String description) {
        super(sensorClass, identifier, description);
        this.setIsOn(bState);
        this.setValue(temperature);
    }

    @Override
    public FirebaseSensor asFirebaseSensor() {
        return new FirebaseSensor(sensorClass, identifier, listOf(new SensorValue(listOf(value, bState ? 1 : 0))));
    }

    public boolean isOn() {
        return this.bState;
    }

    public void setIsOn(boolean bState) {
        this.bState = bState;
    }


    public String toString() {

        String szValue = String.valueOf(value);
        if (szValue.length() == 1) {
            szValue = "0" + szValue;
        }

        String szState = "00";
        if (this.bState == true) {
            szState = "01";
        }

        return super.toString() + szValue + szState;
    }
}
