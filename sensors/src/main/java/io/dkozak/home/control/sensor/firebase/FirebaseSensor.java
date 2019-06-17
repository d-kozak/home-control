package io.dkozak.home.control.sensor.firebase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "sensorId")
public class FirebaseSensor implements Serializable, Comparable<FirebaseSensor> {
    private int sensorType;
    private int sensorId;
    private List<List<Integer>> values;

    @Override
    public int compareTo(FirebaseSensor other) {
        return Comparator.comparingInt(FirebaseSensor::getSensorId)
                         .compare(this, other);
    }
}
