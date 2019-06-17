package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.*;
import io.dkozak.home.control.utils.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

public class Client implements Runnable {

    private Socket myClientSocket = null;
    private DataInputStream myDataInputStream = null;
    private PrintWriter myOutputStream;

    private boolean bIsClosed = false;
    private List<Sensor> mSensors;

    public Client(List<Sensor> mSensors) {

        this.mSensors = mSensors;

        int portNumber = 3000;
        String host = "localhost";

        try {

            Log.message("Opening socket to server: " + host + " " + portNumber);

            this.myClientSocket = new Socket(host, portNumber);
            OutputStream outputStream = this.myClientSocket.getOutputStream();
            this.myOutputStream = new PrintWriter(new OutputStreamWriter(outputStream));
            this.myDataInputStream = new DataInputStream(myClientSocket.getInputStream());

        } catch (UnknownHostException e) {
            Log.message("Don't know about host " + host);
        } catch (IOException e) {
            e.printStackTrace();
            Log.message("Couldn't get I/O for the connection to the host " + host);
        }

        if ((myClientSocket != null) &&
                (myOutputStream != null) &&
                (myDataInputStream != null)) {

            try {

                while (!bIsClosed) {

                    try {

                        Log.message("Sleeping for 60 seconds");
                        Thread.sleep(10000);

                        // Generate random event
                        String szMessage = this.generateRandomData();
                        Log.message("Writing data to server: " + szMessage);

                        this.myOutputStream.write(szMessage + "\n");
                        this.myOutputStream.flush();
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted Exception");
                    }
                }

                Log.message("Closing connection");

                myOutputStream.close();
                myDataInputStream.close();
                myClientSocket.close();

            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void run() {

        String szResponseLine = "";

        try {

            if (this.myDataInputStream != null) {
                while ((szResponseLine = myDataInputStream.readLine()) != null) {

                    Log.message("Received data: " + szResponseLine);

                    if (szResponseLine.contains("exit") == true) {
                        break;
                    }

                    // Sensor data must be "CLASS" "ID" "VALUE" For example: "010120"
                    Sensor mSensor = parseData(szResponseLine);

                    if (mSensor == null) {
                        // do not do anything, we have received some invalid data
                    } else {
                        // Update current data
                        this.updateSensorData(mSensor);
                    }
                }
            }
            this.bIsClosed = true;

        } catch (IOException e) {
            Log.message("Exception while reading data from server: " + e);
        }
    }


    private Sensor parseData(String szResponseLine) {

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


    private void updateSensorData(Sensor mSensor) {

        Log.message("Updating io.dkozak.home.control.sensor data: " + mSensor.toString());

        for (int nIndex = 0; nIndex < mSensors.size(); nIndex++) {

            Sensor mListSensor = mSensors.get(nIndex);
            if ((mListSensor.getSensorClass() == mSensor.getSensorClass()) &&
                    (mListSensor.getIdentifier() == mSensor.getIdentifier())) {

                switch (mSensor.getSensorClass()) {
                    case 0:
                    case 1:
                        mListSensor.setValue(mSensor.getValue());
                        break;

                    // door
                    case 2:
                        ((Door) mListSensor).setIsOpen(((Door) mSensor).isOpen());
                        break;

                    // Light
                    case 3:
                        ((Light) mListSensor).setIsOn(((Light) mSensor).isOn());
                        break;

                    case 4:
                        mListSensor.setValue(mSensor.getValue());
                        ((HVAC) mListSensor).setIsOn(((HVAC) mSensor).isOn());
                        break;
                }
            }
        }

        Log.message("Sensor data updated: " + mSensors.toString());
    }


    public String generateRandomData() {

        Log.message("Generating random data");

        Random m = new Random();
        int nNextSensorPos = m.nextInt(this.mSensors.size() - 1);

        Sensor mSensor = this.mSensors.get(nNextSensorPos);
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
