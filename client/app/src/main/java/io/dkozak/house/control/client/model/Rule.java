package io.dkozak.house.control.client.model;

import java.util.Objects;

public class Rule {
    private String id;
    private int sensorId;
    private int threshold;
    private Comparison comparison;
    private int offset;
    private String userId;

    public Rule() {
    }

    public Rule(int sensorId, int threshold, Comparison comparison, int offset, String userId) {
        this.sensorId = sensorId;
        this.threshold = threshold;
        this.comparison = comparison;
        this.offset = offset;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "sensorId=" + sensorId +
                ", threshold=" + threshold +
                ", comparison=" + comparison +
                ", offset=" + offset +
                ", userId='" + userId + '\'' +
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
                Objects.equals(userId, rule.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sensorId, threshold, comparison, offset, userId);
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


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
