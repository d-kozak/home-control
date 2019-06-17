package io.dkozak.home.control.server;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.server.firebase.FirebaseConnector;
import io.dkozak.home.control.utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.util.Set;

public class GatewayConnector {

    public static void connect(FirebaseConnector firebase) {
        try (var serverSocket = new ServerSocket(ServerConfig.SERVER_PORT);
        ) {
            System.out.println("Waiting for client");
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

            var sensorTypes = (Set<Sensor>) objectStream.readObject();

            Log.message("Loaded sensor types: " + sensorTypes);

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void readSensorData(InputStream inputStream, FirebaseConnector firebase) throws IOException {
        System.out.println("Reading data");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String message;
        while ((message = reader.readLine()) != null) {
            System.out.println("Received: " + message);
        }
        System.out.println("Exiting");
    }
}
