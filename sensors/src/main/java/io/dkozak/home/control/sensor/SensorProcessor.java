package io.dkozak.home.control.sensor;

import io.dkozak.home.control.sensor.type.*;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Random;

@Log
public class SensorProcessor {

    public static Sensor parseData(String szResponseLine) {

        log.info("Parsing data: " + szResponseLine);

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


    public static void updateSensorData(Sensor newValues, List<Sensor> sensors) {

        log.info("Updating io.dkozak.home.control.sensor data: " + newValues.toString());

        for (int nIndex = 0; nIndex < sensors.size(); nIndex++) {

            Sensor mListSensor = sensors.get(nIndex);
            if ((mListSensor.getSensorClass() == newValues.getSensorClass()) &&
                    (mListSensor.getIdentifier() == newValues.getIdentifier())) {

                switch (newValues.getSensorClass()) {
                    case Blinder:
                    case Temperature:
                        mListSensor.setValue(newValues.getValue());
                        break;

                    // door
                    case Door:
                        ((Door) mListSensor).setIsOpen(((Door) newValues).isOpen());
                        break;

                    // Light
                    case Light:
                        ((Light) mListSensor).setIsOn(((Light) newValues).isOn());
                        break;

                    case HVAC:
                        mListSensor.setValue(newValues.getValue());
                        ((HVAC) mListSensor).setIsOn(((HVAC) newValues).isOn());
                        break;
                }
            }
        }

        log.info("Sensor data updated: " + sensors.toString());
    }


    public static String generateRandomData(List<Sensor> sensors) {

        log.info("Generating random data");

        Random m = new Random();
        int nNextSensorPos = m.nextInt(sensors.size() - 1);

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

        log.info("Generated data: " + mSensor.toString());

        return mSensor.toString();
    }
}
