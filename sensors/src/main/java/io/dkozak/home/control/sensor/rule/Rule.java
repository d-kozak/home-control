package io.dkozak.home.control.sensor.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    public int threshold;
    public int comparisonId;
    public int offset;
    public int userId;

    public Rule(int threshold, int comparisonId) {
        this(threshold, comparisonId, 0, 0);
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
