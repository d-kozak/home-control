package io.dkozak.home.control.gateway;

import io.dkozak.home.control.sensor.Sensor;
import io.dkozak.home.control.sensor.SensorProcessor;
import io.dkozak.home.control.utils.Result;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.dkozak.home.control.gateway.GatewayConfig.HOST;
import static io.dkozak.home.control.gateway.GatewayConfig.PORT_NUMBER;

@Log
public class Client {

    public static final Object LOCK = new Object();

    public static void startCommunication(List<Sensor> sensors) {
        try (var socket = connectToServer(HOST, PORT_NUMBER);
             var bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var outputStream = socket.getOutputStream()
        ) {
            var isCancelled = new AtomicBoolean(false);

            sendSensorInfo(sensors, outputStream);
            CompletableFuture.supplyAsync(() -> simulateSensors(sensors, outputStream, isCancelled));
            listenToServer(sensors, bufferedReader, isCancelled);

        } catch (IOException e) {
            log.info("IO exception");
            e.printStackTrace();
        }
    }

    private static void sendSensorInfo(List<Sensor> sensors, OutputStream outputStream) throws IOException {
        var objectOutputStream = new ObjectOutputStream(outputStream);
        var firebaseSensors = sensors.stream()
                                     .map(Sensor::asFirebaseSensor)
                                     .collect(Collectors.toSet());
        log.info("Sending sensors : " + firebaseSensors);
        objectOutputStream.writeObject(firebaseSensors);
    }

    public static Result<String, Exception> simulateSensors(List<Sensor> sensors, OutputStream outputStream, AtomicBoolean isCancelled) {
        var printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        while (!isCancelled.get()) {
            try {
                log.info("Sleeping for 3 seconds");
                Thread.sleep(3000);

                String szMessage;

                synchronized (LOCK) {
                    // Generate random event
                    szMessage = SensorProcessor.generateRandomData(sensors);
                }

                log.info("Writing data to server: " + szMessage);

                printWriter.write(szMessage + "\n");
                printWriter.flush();
            } catch (InterruptedException ex) {
                log.info("Interrupted");
                ex.printStackTrace();
                isCancelled.set(true);
                return new Result<>(null, ex);
            } catch (Exception ex) {
                log.severe("Unexpected exception " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        log.info("Simulating sensors finished");
        return new Result<>("OK", null);
    }

    static Socket connectToServer(String host, int portNumber) {
        try {
            log.info("Opening socket to server: " + host + " " + portNumber);
            return new Socket(host, portNumber);
        } catch (UnknownHostException e) {
            log.info("Don't know about host " + host);
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("Couldn't get I/O for the connection to the host " + host);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static Result<String, Exception> listenToServer(List<Sensor> sensors, BufferedReader inputStream, AtomicBoolean isCancelled) {
        try {
            String line;
            while (!isCancelled.get() && (line = inputStream.readLine()) != null) {
                log.info("Received: " + line);

                if (line.equals("exit")) {
                    log.info("Exiting...");
                    break;
                }
                var updateRequest = SensorProcessor.parseUpdateRequest(line);
                if (updateRequest != null) {
                    synchronized (LOCK) {
                        SensorProcessor.updateSensorData(updateRequest, sensors);
                    }

                } else {
                    log.info("Could not parse received data: " + line);
                }

            }
            isCancelled.set(true);
            return new Result<>("OK", null);
        } catch (IOException ex) {
            log.info("IO exception");
            ex.printStackTrace();
            return new Result<>(null, ex);
        }
    }


}
