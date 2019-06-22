package io.dkozak.home.control.sensor;

import io.dkozak.home.control.sensor.firebase.SensorUpdateRequest;
import io.dkozak.home.control.sensor.type.*;
import lombok.extern.java.Log;
import lombok.var;

@Log
public class SensorParser {

    public static String serialize(Sensor toSerialize) {
        return toSerialize.toString();
    }

    public static Sensor parseData(String szResponseLine) {
        log.finer("Parsing data: " + szResponseLine);

        if (szResponseLine == null || szResponseLine.length() < 5) {
            return null;
        }

        Sensor mSensor = null;

        // Parse io.dkozak.home.control.sensor class
        String szSensorClass = szResponseLine.substring(0, 2);
        int nSensorClass = Integer.parseInt(szSensorClass);

        String szSensorId = szResponseLine.substring(2, 4);
        int nSensorId = Integer.parseInt(szSensorId);

        switch (SensorClass.values()[nSensorClass]) {
            case Temperature:
                String szTemperature = szResponseLine.substring(4, 6);
                int nSensorTemperature = Integer.parseInt(szTemperature);
                mSensor = new Temperature(nSensorId, nSensorTemperature, "");
                break;

            case Blinder:
                String szBlinderPercentage = szResponseLine.substring(4, 7);
                int nBlinderPercentage = Integer.parseInt(szBlinderPercentage);
                mSensor = new Blinder(nSensorId, nBlinderPercentage, "");
                break;

            case Door:
                String szDoorState = szResponseLine.substring(4, 6);
                int nDoorState = Integer.parseInt(szDoorState);

                boolean bIsDoorOpen = false;
                if (nDoorState == 1) {
                    bIsDoorOpen = true;
                }

                mSensor = new Door(nSensorId, bIsDoorOpen, "");
                break;

            case Light:
                String szLightState = szResponseLine.substring(4, 6);
                int nLightState = Integer.parseInt(szLightState);

                boolean bIsLightOn = false;
                if (nLightState == 1) {
                    bIsLightOn = true;
                }

                mSensor = new Light(nSensorId, bIsLightOn, "");
                break;

            case HVAC:
                String szHVACState = szResponseLine.substring(6, 8);
                String szHVACTemperature = szResponseLine.substring(4, 6);
                int nHVACState = Integer.parseInt(szHVACState);
                int nHVACTemperature = Integer.parseInt(szHVACTemperature);

                boolean bIsHVACOn = false;
                if (nHVACState == 1) {
                    bIsHVACOn = true;
                }

                mSensor = new HVAC(nSensorId, bIsHVACOn, nHVACTemperature, "");
                break;
        }

        return mSensor;
    }

    public static SensorUpdateRequest parseUpdateRequest(String input) {
        log.finer("Parsing " + input);

        if (input.length() != 3) {
            log.severe("Invalid length of input message" + input + ", should be 3");
            return null;
        }

        try {
            var sensorId = Integer.parseInt(input.substring(0, 2));
            int parsed = Integer.parseInt(input.substring(2, 3));
            boolean newValue;
            if (parsed == 0) {
                newValue = false;
            } else if (parsed == 1) {
                newValue = true;
            } else {
                log.severe("Invalid new value, can be only 1 or 0, that is true or false");
                throw new NumberFormatException();
            }
            return new SensorUpdateRequest(null, sensorId, newValue);
        } catch (NumberFormatException ex) {
            log.severe("Failed to parse " + input);
        }
        return null;
    }
}
