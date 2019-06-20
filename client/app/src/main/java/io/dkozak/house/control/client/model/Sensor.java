package io.dkozak.house.control.client.model;

import java.util.List;
import java.util.Objects;

public class Sensor {
    private int sensorId;
    private int sensorType;
    private String description;
    private List<List<Integer>> values;

    public Sensor() {
    }

    public Sensor(int sensorId, int sensorType, String description, List<List<Integer>> values) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.description = description;
        this.values = values;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public List<List<Integer>> getValues() {
        return values;
    }

    public void setValues(List<List<Integer>> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return sensorId == sensor.sensorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId);
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId=" + sensorId +
                ", sensorType=" + sensorType +
                ", description=" + description +
                ", values=" + values +
                '}';
    }
}
