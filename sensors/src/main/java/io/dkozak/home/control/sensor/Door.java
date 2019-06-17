package io.dkozak.home.control.sensor;

public class Door extends Sensor {

    private boolean bState = false;

    public Door(int sensorClass, int identifier, boolean bState, String description) {
        super(sensorClass, identifier, description);
        this.setIsOpen(bState);
    }

    public boolean isOpen() {
        return this.bState;
    }

    public void setIsOpen(boolean bState) {
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
