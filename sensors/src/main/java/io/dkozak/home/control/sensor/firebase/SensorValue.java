package io.dkozak.home.control.sensor.firebase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorValue implements Serializable {
    private List<Integer> value;
}
