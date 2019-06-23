package io.dkozak.home.control.sensor.firebase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorUpdateRequest {
    private String user;
    private int sensorId;
    private int index;
    private int value;
}
