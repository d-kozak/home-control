package io.dkozak.house.control.client.model;

import java.util.List;
import java.util.Objects;

public class SensorType {
    private int id;
    private String name;
    private List<SensorValue> valueTypes;

    public SensorType() {
    }

    public SensorType(int id, String name, List<SensorValue> valueTypes) {
        this.id = id;
        this.name = name;
        this.valueTypes = valueTypes;
    }

    public List<SensorValue> getValueTypes() {
        return valueTypes;
    }

    public void setValueTypes(List<SensorValue> valueTypes) {
        this.valueTypes = valueTypes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        SensorType that = (SensorType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SensorType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", valueTypes=" + valueTypes +
                '}';
    }

    private int getValueIndex(ValueType type) {
        for (int i = 0; i < valueTypes.size(); i++) {
            if (valueTypes.get(i).getType() == type)
                return i;
        }
        return -1;
    }

    public int getBoolValueIndex() {
        return getValueIndex(ValueType.BOOL);
    }

    public int getIntValueIndex() {
        return getValueIndex(ValueType.INT);
    }
}
