package io.dkozak.house.control.client.model;

import java.util.Objects;

public class SensorUpdateRequest {
    private String user;
    private int sensorId;
    private int newValue;
    private int index;

    public SensorUpdateRequest() {
    }


    public SensorUpdateRequest(String user, int sensorId, int newValue, int index) {
        this.user = user;
        this.sensorId = sensorId;
        this.newValue = newValue;
        this.index = index;
    }

    public int getNewValue() {
        return newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorUpdateRequest that = (SensorUpdateRequest) o;
        return sensorId == that.sensorId &&
                newValue == that.newValue &&
                index == that.index &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, sensorId, newValue, index);
    }

    @Override
    public String toString() {
        return "SensorUpdateRequest{" +
                "user='" + user + '\'' +
                ", sensorId=" + sensorId +
                ", newValue=" + newValue +
                ", index=" + index +
                '}';
    }
}
