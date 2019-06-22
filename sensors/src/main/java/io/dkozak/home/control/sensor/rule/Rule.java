package io.dkozak.home.control.sensor.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.var;

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
        switch (comparison) {
            case GT:
                return value > threshold;
            case GE:
                return value >= threshold;
            case EQ:
                return value == threshold;
            case LE:
                return value <= threshold;
            case LT:
                return value < threshold;
            default:
                throw new IllegalArgumentException("Should be exhaustive");
        }
    }
}
