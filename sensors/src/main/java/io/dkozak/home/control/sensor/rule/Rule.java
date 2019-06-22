package io.dkozak.home.control.sensor.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;

@Log
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    private int sensorId;
    private int threshold;
    private Comparison comparison;
    private int offset;
    private String userId;

    public Rule(int sensorId, int threshold, Comparison comparison, String userId) {
        this(sensorId, threshold, comparison, 0, userId);
    }

    public boolean isTriggered(List<Integer> sensorValues) {
        if (offset >= sensorValues.size()) {
            log.severe("Invalid offset " + offset + "at rule " + this + " for input " + sensorValues);
            return false;
        }
        var value = sensorValues.get(offset);
        return switch (comparison) {
            case GT -> value > threshold;
            case GE -> value >= threshold;
            case EQ -> value == threshold;
            case LE -> value <= threshold;
            case LT -> value < threshold;
        };
    }
}
