package io.dkozak.house.control.client.model;

import java.util.Objects;

public class Rule {
    public String id;
    public int sensorId;
    public int threshold;
    public Comparison comparison;
    public int offset;
    public String deviceId;

    public Rule() {
    }

    public Rule(int sensorId, int threshold, Comparison comparison, int offset, String deviceId) {
        this.sensorId = sensorId;
        this.threshold = threshold;
        this.comparison = comparison;
        this.offset = offset;
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "sensorId=" + sensorId +
                ", threshold=" + threshold +
                ", comparison=" + comparison +
                ", offset=" + offset +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return sensorId == rule.sensorId &&
                threshold == rule.threshold &&
                offset == rule.offset &&
                Objects.equals(id, rule.id) &&
                comparison == rule.comparison &&
                Objects.equals(deviceId, rule.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sensorId, threshold, comparison, offset, deviceId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
