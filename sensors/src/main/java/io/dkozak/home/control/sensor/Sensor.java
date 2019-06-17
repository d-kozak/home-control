package io.dkozak.home.control.sensor;

import lombok.Data;

@Data
public abstract class Sensor {

    protected int sensorClass;
    protected int identifier;
    protected int value = 0;
    protected String description;
    protected SensorType sensorType;


    public Sensor(int sensorClass, int identifier, String description) {
        this.sensorClass = sensorClass;
        this.identifier = identifier;
        this.description = description;
        this.sensorType = new SensorType(sensorClass, this.getClass()
                                                          .getSimpleName());
    }

    public String toString() {
        String szSensorClass = String.valueOf(sensorClass);
        if (szSensorClass.length() == 1) {
            szSensorClass = "0" + szSensorClass;
        }

        String szSensorIdentifier = String.valueOf(identifier);
        if (szSensorIdentifier.length() == 1) {
            szSensorIdentifier = "0" + szSensorIdentifier;
        }

        return szSensorClass + szSensorIdentifier;
    }
}
