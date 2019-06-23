package io.dkozak.home.control.sensor;

import io.dkozak.home.control.sensor.firebase.SensorUpdateRequest;
import io.dkozak.home.control.sensor.type.Door;
import io.dkozak.home.control.sensor.type.HVAC;
import io.dkozak.home.control.sensor.type.Light;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Random;

@Log
public class SensorProcessor {


    public static Sensor updateSensorData(SensorUpdateRequest request, List<Sensor> sensors) {
        log.finer("Updating io.dkozak.home.control.sensor data: " + request);
        for (Sensor sensor : sensors) {
            if (sensor.getIdentifier() == request.getSensorId()) {
                switch (sensor.getSensorClass()) {
                    case Blinder:
                    case Temperature:
                    case Door:
                    case Light:
                        if (request.getIndex() == 0) {
                            sensor.setValue(request.getValue());
                        } else {
                            log.severe("Unsupported index " + request.getIndex() + " for sensor " + sensor);
                        }
                        break;

                    case HVAC:
                        var hvac = ((HVAC) sensor);
                        switch (request.getIndex()) {
                            case 0 -> {
                                hvac.setValue(request.getValue());
                            }
                            case 1 -> {
                                hvac.setIsOn(request.getValue() > 0);
                            }
                            default -> {
                                log.severe("Unsupported index " + request.getIndex() + " for sensor " + hvac);
                            }
                        }
                        break;
                }
                log.finer("Sensor data updated: " + sensor.toString());
                return sensor;
            }
        }
        log.severe("No sensor was updated!");
        return null;
    }


    public static String generateRandomData(List<Sensor> sensors) {
        if (sensors.isEmpty()) {
            log.severe("No sensors, nothing to generate");
            return null;
        }
        log.finer("Generating random data");

        Random m = new Random();
        int nNextSensorPos = 0;
        if (sensors.size() > 1) {
            nNextSensorPos = m.nextInt(sensors.size() - 1);
        }

        Sensor mSensor = sensors.get(nNextSensorPos);
        switch (mSensor.getSensorClass()) {

            // Temperature io.dkozak.home.control.sensor
            case Temperature:

                int nTemperature = mSensor.getValue();
                if (nTemperature == 0) {
                    nTemperature = 20;
                }

                mSensor.setValue(nTemperature - 1);
                break;

            // Blinder
            case Blinder:
                int nLevel = mSensor.getValue();
                if (nLevel == 0) {
                    nLevel = 100;
                }

                mSensor.setValue(nLevel - 1);
                break;

            // Door
            case Door:
                if (((Door) mSensor).isOpen() == true) {
                    ((Door) mSensor).setIsOpen(false);
                } else {
                    ((Door) mSensor).setIsOpen(true);
                }
                break;

            // Lights
            case Light:
                if (((Light) mSensor).isOn() == true) {
                    ((Light) mSensor).setIsOn(false);
                } else {
                    ((Light) mSensor).setIsOn(true);
                }
                break;

            // HVAC
            case HVAC:
                if (((HVAC) mSensor).isOn() == true) {

                    if (mSensor.getValue() != 0) {
                        mSensor.setValue(mSensor.getValue() - 1);
                    } else {
                        ((HVAC) mSensor).setIsOn(false);
                        mSensor.setValue(20);
                    }
                } else {
                    ((HVAC) mSensor).setIsOn(true);
                }
                break;
        }

        log.finer("Generated data: " + mSensor.toString());

        return mSensor.toString();
    }
}
