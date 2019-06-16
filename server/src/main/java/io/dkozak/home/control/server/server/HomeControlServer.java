package io.dkozak.home.control.server.server;


public class HomeControlServer {

    public static void main(String[] args) {
        BusConnector connector = new BusConnector();
        connector.connect();
    }

}
