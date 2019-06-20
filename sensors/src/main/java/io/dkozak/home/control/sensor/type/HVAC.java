package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorClass;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class HVAC extends Sensor {

    private boolean bState = false;

    public HVAC(int identifier, boolean bState, int temperature, String description) {
        super(SensorClass.HVAC, identifier, description);
        this.setIsOn(bState);
        this.setValue(temperature);
    }

    public boolean isOn() {
        return this.bState;
    }

    public void setIsOn(boolean bState) {
        this.bState = bState;
    }

    @Override
    public List<Integer> getData() {
        return listOf(value, bState ? 1 : 0);
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
