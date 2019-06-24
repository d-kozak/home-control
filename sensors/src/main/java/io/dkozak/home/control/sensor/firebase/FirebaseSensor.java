package io.dkozak.home.control.sensor.firebase;

import io.dkozak.home.control.sensor.rule.Rule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "sensorId")
public class FirebaseSensor implements Serializable, Comparable<FirebaseSensor> {
    private int sensorType;
    private int sensorId;
    private String description;
    private List<List<Integer>> values;

    private Map<String, Map<String, Rule>> rule;

    @Override
    public int compareTo(FirebaseSensor other) {
        return Comparator.comparingInt(FirebaseSensor::getSensorId)
                         .compare(this, other);
    }
}
