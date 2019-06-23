package io.dkozak.home.control.sensor.firebase;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SensorValue {
    private ValueType type;
    private String name;
}
