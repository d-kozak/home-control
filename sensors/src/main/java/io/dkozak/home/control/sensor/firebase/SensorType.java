package io.dkozak.home.control.sensor.firebase;


import io.dkozak.home.control.SensorValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorType implements Serializable, Comparable<SensorType> {
    private int id;
    private String name;
    private List<SensorValue> valueTypes;

    @Override
    public int compareTo(SensorType other) {
        return Comparator.comparingInt(SensorType::getId)
                         .compare(this, other);
    }
}
