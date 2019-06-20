package io.dkozak.house.control.client.model;

import java.util.Objects;

public class SensorUpdateRequest {
    private String user;
    private int sensorId;
    private boolean newValue;

    public SensorUpdateRequest() {
    }

    public SensorUpdateRequest(String user, int sensorId, boolean newValue) {
        this.user = user;
        this.sensorId = sensorId;
        this.newValue = newValue;
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

    public boolean isNewValue() {
        return newValue;
    }

    public void setNewValue(boolean newValue) {
        this.newValue = newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorUpdateRequest that = (SensorUpdateRequest) o;
        return sensorId == that.sensorId &&
                newValue == that.newValue &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, sensorId, newValue);
    }

    @Override
    public String toString() {
        return "SensorUpdateRequest{" +
                "user='" + user + '\'' +
                ", sensorId=" + sensorId +
                ", newValue=" + newValue +
                '}';
    }
}
