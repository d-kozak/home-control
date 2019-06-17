package io.dkozak.home.control.server;


import io.dkozak.home.control.server.firebase.FirebaseConnector;

public class Server {

    public static void main(String[] args) {

        var firebase = new FirebaseConnector();

        GatewayConnector.connect(firebase);
    }


}
