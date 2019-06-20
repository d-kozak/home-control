package io.dkozak.home.control.sensor.type;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorClass;

import java.util.List;

import static io.dkozak.home.control.utils.ListUtils.listOf;

public class Door extends Sensor {

    public Door(int identifier, boolean bState, String description) {
        super(SensorClass.Door, identifier, description);
        this.setIsOpen(bState);
    }

    @Override
    public List<Integer> getData() {
        return listOf(value);
    }

    public boolean isOpen() {
        return this.value != 0;
    }

    public void setIsOpen(boolean isOpen) {
        this.value = isOpen ? 1 : 0;
    }

    public String toString() {
        return super.toString() + (isOpen() ? "01" : "00");
    }
}
