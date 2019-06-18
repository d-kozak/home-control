package io.dkozak.house.control.client.view.sensorlist;

import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public interface OnSensorClickedListener {
    void onClick(Sensor sensor, SensorType sensorType);
}
