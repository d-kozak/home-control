package pt.ipp.estg.home.control.bus;

import pt.ipp.estg.home.control.sensor.*;
import pt.ipp.estg.home.control.utils.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class HomeControlBus {

    static ArrayList<Sensor> mSensors = new ArrayList<>();

    static Socket myClientSocket = null;
    static PrintStream myPrintStream = null;
    static DataInputStream myDataInputStream = null;
    static BufferedReader myBufferedReader = null;
    static boolean bIsClosed = false;

    static Client mClient;

    public static void main(String[] args) {

        Log.message("Starting client");

        initSensors();
        connectToServer();
    }


    public static void initSensors() {

        Log.message("Initializing pt.ipp.estg.home.control.sensor list");

        mSensors.add(new Temperature(0, 1, 20, "Room 1"));
        mSensors.add(new Temperature(0, 2, 20, "Room 2"));
        mSensors.add(new Temperature(0, 3, 20, "Room 3"));

        mSensors.add(new Blinder(1, 1, 0, "Room 1"));
        mSensors.add(new Blinder(1, 2, 100, "Room 2"));
        mSensors.add(new Blinder(1, 3, 50, "Room 3"));

        mSensors.add(new Door(2, 1, false, "Room 1"));
        mSensors.add(new Door(2, 2, true, "Room 2"));
        mSensors.add(new Door(2, 3, true, "Room 3"));

        mSensors.add(new Light(3, 1, false, "Room 1"));
        mSensors.add(new Light(3, 2, false, "Room 2"));
        mSensors.add(new Light(3, 3, true, "Room 3"));

        mSensors.add(new HVAC(4, 1, false, 0, "Room 1"));
        mSensors.add(new HVAC(4, 2, false, 0, "Room 2"));
        mSensors.add(new HVAC(4, 3, true, 20, "Room 3"));

        Log.message("Sensor list: " + mSensors.toString());
    }


    public static void connectToServer() {

        Log.message("Starting connection to server");

        mClient = new Client(mSensors);
        new Thread(mClient).start();
    }
}
