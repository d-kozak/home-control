package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;

public class Light extends Sensor {

    private boolean bState = false;

    public Light(int sensorClass, int identifier, boolean bState, String description) {
        super(sensorClass, identifier, description);
        this.setIsOn(bState);
    }

    public boolean isOn() {
        return this.bState;
    }

    public void setIsOn(boolean bState) {
        this.bState = bState;
    }

    public String toString() {

        String szValue = "00";
        if (this.bState == true) {
            szValue = "01";
        }

        return super.toString() + szValue;
    }
}
