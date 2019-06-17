package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorProcessor;
import io.dkozak.home.control.utils.Log;
import io.dkozak.home.control.utils.Result;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.dkozak.home.control.gateway.GatewayConfig.HOST;
import static io.dkozak.home.control.gateway.GatewayConfig.PORT_NUMBER;

public class Client {

    public static void startCommunication(CopyOnWriteArrayList<Sensor> sensors) {
        try (var socket = connectToServer(HOST, PORT_NUMBER);
             var bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var outputStream = socket.getOutputStream()
        ) {
            var isCancelled = new AtomicBoolean(false);

            sendSensorInfo(sensors, outputStream);
            CompletableFuture.supplyAsync(() -> simulateSensors(sensors, outputStream, isCancelled));
            listenToServer(sensors, bufferedReader, isCancelled);

        } catch (IOException e) {
            Log.message("IO exception");
            e.printStackTrace();
        }
    }

    private static void sendSensorInfo(CopyOnWriteArrayList<Sensor> sensors, OutputStream outputStream) throws IOException {
        var objectOutputStream = new ObjectOutputStream(outputStream);

        var sensorTypes = sensors.stream()
                                 .map(Sensor::getSensorType)
                                 .collect(Collectors.toSet());

        Log.message("Sending sensor types: " + sensorTypes);
        objectOutputStream.writeObject(sensorTypes);
    }

    public static Result<String, Exception> simulateSensors(CopyOnWriteArrayList<Sensor> sensors, OutputStream outputStream, AtomicBoolean isCancelled) {
        var printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        while (!isCancelled.get()) {
            try {
                Log.message("Sleeping for 10 seconds");
                Thread.sleep(10000);

                // Generate random event
                String szMessage = SensorProcessor.generateRandomData(sensors);
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

    static Socket connectToServer(String host, int portNumber) {
        try {
            Log.message("Opening socket to server: " + host + " " + portNumber);
            return new Socket(host, portNumber);
        } catch (UnknownHostException e) {
            Log.message("Don't know about host " + host);
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.message("Couldn't get I/O for the connection to the host " + host);
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

                var newData = SensorProcessor.parseData(line);
                if (newData != null) {
                    SensorProcessor.updateSensorData(newData, sensors);
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


}
