package io.dkozak.house.control.client.model;

import java.util.Objects;

public class SensorValue {
    private ValueType type;
    private String name;

    public SensorValue() {
    }

    public SensorValue(ValueType type, String name) {
        this.type = type;
        this.name = name;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorValue that = (SensorValue) o;
        return type == that.type &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return name;
    }
}
