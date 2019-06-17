package io.dkozak.home.control.sensor;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorType implements Serializable {
    private int id;
    private String name;
}
