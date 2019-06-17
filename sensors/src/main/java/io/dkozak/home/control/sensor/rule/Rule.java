package io.dkozak.home.control.sensor.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    public int sensorId;
    public int threshold;
    public int comparisonId;
    public int offset;
    public String deviceId;

    public Rule(int sensorId, int threshold, int comparisonId, String deviceId) {
        this(sensorId, threshold, comparisonId, 0, deviceId);
    }

    public boolean isTriggered(List<Integer> sensorValues) {
        var value = sensorValues.get(offset);
        return switch (Comparison.values()[comparisonId]) {
            case GT -> value > threshold;
            case GE -> value >= threshold;
            case EQ -> value == threshold;
            case LE -> value <= threshold;
            case LT -> value < threshold;
        };
    }
}
