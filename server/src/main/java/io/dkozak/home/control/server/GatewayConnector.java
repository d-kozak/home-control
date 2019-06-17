package io.dkozak.home.control.server;

import io.dkozak.home.control.sensor.SensorProcessor;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.sensor.firebase.SensorType;
import io.dkozak.home.control.server.firebase.FirebaseConnector;
import io.dkozak.home.control.utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.util.Set;

public class GatewayConnector {

    public static void connect(FirebaseConnector firebase) {
        try (var serverSocket = new ServerSocket(ServerConfig.SERVER_PORT);
        ) {
            Log.message("Waiting for gateway");
            try (var client = serverSocket.accept();
                 var inputStream = client.getInputStream();
                 var outputStream = client.getOutputStream()
            ) {

                loadSensorTypes(inputStream, firebase);
                readSensorData(inputStream, firebase);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void loadSensorTypes(InputStream inputStream, FirebaseConnector firebase) throws IOException {
        try {
            var objectStream = new ObjectInputStream(inputStream);

            var sensorTypes = (Set<SensorType>) objectStream.readObject();
            Log.message("Loaded sensor types: " + sensorTypes);
            firebase.updateList(sensorTypes, SensorType.class, "sensor-types");

            var sensors = (Set<FirebaseSensor>) objectStream.readObject();
            Log.message("Loaded sensors: " + sensors);
            firebase.updateList(sensors, FirebaseSensor.class, "sensor");

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void readSensorData(InputStream inputStream, FirebaseConnector firebase) throws IOException {
        Log.message("Reading data");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String message;
        while ((message = reader.readLine()) != null) {
            Log.message("Received: " + message);

            var sensorData = SensorProcessor.parseData(message);
            if (sensorData == null) {
                Log.message("Could not parse sensor data");
                continue;
            }
            firebase.newSensorData(sensorData);
            firebase.executeRulesFor(sensorData);
        }
        Log.message("Exiting");
    }
}
