package io.dkozak.home.control.server;

import io.dkozak.home.control.sensor.SensorParser;
import io.dkozak.home.control.sensor.firebase.FirebaseSensor;
import io.dkozak.home.control.server.firebase.FirebaseConnector;
import lombok.extern.java.Log;
import lombok.var;

import java.io.*;
import java.net.ServerSocket;
import java.util.Set;

@Log
public class GatewayConnector {

    public static void connect(FirebaseConnector firebase) {
        try (var serverSocket = new ServerSocket(ServerConfig.SERVER_PORT);
        ) {
            log.info("Waiting for gateway");
            try (var client = serverSocket.accept();
                 var inputStream = client.getInputStream();
                 var outputStream = client.getOutputStream()
            ) {

                loadSensorTypes(inputStream, firebase);
                setupSensorUpdateListener(outputStream, firebase);
                readSensorData(inputStream, firebase);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setupSensorUpdateListener(OutputStream outputStream, FirebaseConnector firebase) {
        var writer = new OutputStreamWriter(outputStream);
        firebase.onUserRequestedSensorUpdate((request) -> {
            try {
                String message = String.valueOf(request.getSensorId());
                if (message.length() == 1)
                    message = '0' + message;
                message += (request.isNewValue() ? 1 : 0) + '\n';
                log.finer("Sending message " + message);
                writer.write(message);
                writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                log.severe("Could not send update request to the gateway");
            }
        });
    }

    private static void loadSensorTypes(InputStream inputStream, FirebaseConnector firebase) throws IOException {
        try {
            var objectStream = new ObjectInputStream(inputStream);

            var sensors = (Set<FirebaseSensor>) objectStream.readObject();
            log.info("Loaded sensors: " + sensors);
            firebase.updateList(sensors, FirebaseSensor.class, "sensor");

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void readSensorData(InputStream inputStream, FirebaseConnector firebase) throws IOException {
        log.info("Reading data");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String message;
        while ((message = reader.readLine()) != null) {
            log.finer("Received: " + message);

            var sensorData = SensorParser.parseData(message);
            if (sensorData == null) {
                log.info("Could not parse sensor data");
                continue;
            }
            firebase.newSensorData(sensorData);
        }
        log.info("Exiting");
    }
}
