package io.dkozak.home.control.server.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class BusConnector {

    public void connect() {
        try {
            int port = 3000;
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Waiting for client");
            Socket client = serverSocket.accept();
            handleClient(client);


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleClient(Socket client) throws IOException {
        System.out.println("Reading data");
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String message;
        while ((message = reader.readLine()) != null) {
            System.out.println("Received: " + message);
        }
        System.out.println("Exiting");
    }
}
