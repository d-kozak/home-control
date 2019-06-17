package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.*;
import io.dkozak.home.control.utils.Log;
import io.dkozak.home.control.utils.Result;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    public static final int PORT_NUMBER = 3000;
    public static final String HOST = "localhost";

    public static void startCommunication(CopyOnWriteArrayList<Sensor> sensors) {
        try (var socket = connectToServer();
             var bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            var isCancelled = new AtomicBoolean(false);

            CompletableFuture.supplyAsync(() -> simulateSensors(sensors, printWriter, isCancelled));
            listenToServer(sensors, bufferedReader, isCancelled);

        } catch (IOException e) {
            Log.message("IO exception");
            e.printStackTrace();
        }
    }

    public static Result<String, Exception> simulateSensors(CopyOnWriteArrayList<Sensor> sensors, PrintWriter printWriter, AtomicBoolean isCancelled) {
        while (!isCancelled.get()) {
            try {
                Log.message("Sleeping for 10 seconds");
                Thread.sleep(10000);

                // Generate random event
                String szMessage = generateRandomData(sensors);
                Log.message("Writing data to server: " + szMessage);

                printWriter.write(szMessage + "\n");
                printWriter.flush();
            } catch (InterruptedException ex) {
                Log.message("Interrupted");
                ex.printStackTrace();
                isCancelled.set(true);
                return new Result<>(null, ex);
            }
        }

        return new Result<>("OK", null);
    }

    static Socket connectToServer() {
        try {
            Log.message("Opening socket to server: " + HOST + " " + PORT_NUMBER);
            return new Socket(HOST, PORT_NUMBER);
        } catch (UnknownHostException e) {
            Log.message("Don't know about HOST " + HOST);
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.message("Couldn't get I/O for the connection to the HOST " + HOST);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static Result<String, Exception> listenToServer(CopyOnWriteArrayList<Sensor> sensors, BufferedReader inputStream, AtomicBoolean isCancelled) {
        try {
            String line;
            while (!isCancelled.get() && (line = inputStream.readLine()) != null) {
                Log.message("Received: " + line);

                if (line.equals("exit")) {
                    Log.message("Exiting...");
                    break;
                }

                var newData = parseData(line);
                if (newData != null) {
                    updateSensorData(newData, sensors);
                } else {
                    Log.message("Could not parse received data: " + line);
                }

            }
            isCancelled.set(true);
            return new Result<>("OK", null);
        } catch (IOException ex) {
            Log.message("IO exception");
            ex.printStackTrace();
            return new Result<>(null, ex);
        }
    }

    static Sensor parseData(String szResponseLine) {

        Log.message("Parsing data: " + szResponseLine);

        if (szResponseLine == null || szResponseLine.length() < 5) {
            return null;
        }

        Sensor mSensor = null;

        // Parse io.dkozak.home.control.sensor class
        String szSensorClass = szResponseLine.substring(0, 2);
        int nSensorClass = Integer.parseInt(szSensorClass);

        String szSensorId = szResponseLine.substring(2, 4);
        int nSensorId = Integer.parseInt(szSensorId);

        switch (nSensorClass) {
            case 0:
                String szTemperature = szResponseLine.substring(4, 6);
                int nSensorTemperature = Integer.parseInt(szTemperature);
                mSensor = new Temperature(nSensorClass, nSensorId, nSensorTemperature, "");
                break;

            case 1:
                String szBlinderPercentage = szResponseLine.substring(4, 7);
                int nBlinderPercentage = Integer.parseInt(szBlinderPercentage);
                mSensor = new Blinder(nSensorClass, nSensorId, nBlinderPercentage, "");
                break;

            case 2:
                String szDoorState = szResponseLine.substring(4, 6);
                int nDoorState = Integer.parseInt(szDoorState);

                boolean bIsDoorOpen = false;
                if (nDoorState == 1) {
                    bIsDoorOpen = true;
                }

                mSensor = new Door(nSensorClass, nSensorId, bIsDoorOpen, "");
                break;

            case 3:
                String szLightState = szResponseLine.substring(4, 6);
                int nLightState = Integer.parseInt(szLightState);

                boolean bIsLightOn = false;
                if (nLightState == 1) {
                    bIsLightOn = true;
                }

                mSensor = new Light(nSensorClass, nSensorId, bIsLightOn, "");
                break;

            case 4:
                String szHVACState = szResponseLine.substring(6, 8);
                String szHVACTemperature = szResponseLine.substring(4, 6);
                int nHVACState = Integer.parseInt(szHVACState);
                int nHVACTemperature = Integer.parseInt(szHVACTemperature);

                boolean bIsHVACOn = false;
                if (nHVACState == 1) {
                    bIsHVACOn = true;
                }

                mSensor = new HVAC(nSensorClass, nSensorId, bIsHVACOn, nHVACTemperature, "");
                break;
        }

        return mSensor;
    }


    static void updateSensorData(Sensor newValues, CopyOnWriteArrayList<Sensor> sensors) {

        Log.message("Updating io.dkozak.home.control.sensor data: " + newValues.toString());

        for (int nIndex = 0; nIndex < sensors.size(); nIndex++) {

            Sensor mListSensor = sensors.get(nIndex);
            if ((mListSensor.getSensorClass() == newValues.getSensorClass()) &&
                    (mListSensor.getIdentifier() == newValues.getIdentifier())) {

                switch (newValues.getSensorClass()) {
                    case 0:
                    case 1:
                        mListSensor.setValue(newValues.getValue());
                        break;

                    // door
                    case 2:
                        ((Door) mListSensor).setIsOpen(((Door) newValues).isOpen());
                        break;

                    // Light
                    case 3:
                        ((Light) mListSensor).setIsOn(((Light) newValues).isOn());
                        break;

                    case 4:
                        mListSensor.setValue(newValues.getValue());
                        ((HVAC) mListSensor).setIsOn(((HVAC) newValues).isOn());
                        break;
                }
            }
        }

        Log.message("Sensor data updated: " + sensors.toString());
    }


    static String generateRandomData(CopyOnWriteArrayList<Sensor> sensors) {

        Log.message("Generating random data");

        Random m = new Random();
        int nNextSensorPos = m.nextInt(sensors.size() - 1);

        Sensor mSensor = sensors.get(nNextSensorPos);
        switch (mSensor.getSensorClass()) {

            // Temperature io.dkozak.home.control.sensor
            case 0:

                int nTemperature = mSensor.getValue();
                if (nTemperature == 0) {
                    nTemperature = 20;
                }

                mSensor.setValue(nTemperature - 1);
                break;

            // Blinder
            case 1:
                int nLevel = mSensor.getValue();
                if (nLevel == 0) {
                    nLevel = 100;
                }

                mSensor.setValue(nLevel - 1);
                break;

            // Door
            case 2:
                if (((Door) mSensor).isOpen() == true) {
                    ((Door) mSensor).setIsOpen(false);
                } else {
                    ((Door) mSensor).setIsOpen(true);
                }
                break;

            // Lights
            case 3:
                if (((Light) mSensor).isOn() == true) {
                    ((Light) mSensor).setIsOn(false);
                } else {
                    ((Light) mSensor).setIsOn(true);
                }
                break;

            // HVAC
            case 4:
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

        Log.message("Generated data: " + mSensor.toString());

        return mSensor.toString();
    }
}
