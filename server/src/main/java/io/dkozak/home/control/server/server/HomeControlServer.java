package io.dkozak.home.control.server.server;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.IOException;

public class HomeControlServer {

    public static void main(String[] args) {

        database();
        BusConnector connector = new BusConnector();
        connector.connect();
    }

    private static void database() {
        FirebaseApp firebaseApp = initFirebase("server/src/main/resources/houseconrol-dkozak-firebase-adminsdk-3g93e-e76e35129c.json");
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        database.getReference("user")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println(snapshot.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toString());
                    }
                });
        database.getReference("user")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        System.out.println("new user" + snapshot.toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
        database.getReference("user")
                .push()
                .setValue("ahoj", (error, ref) -> System.out.println("complete " + error));
        System.out.println("listening");
    }

    public static FirebaseApp initFirebase(String firebaseKey) {
        try {
            FileInputStream stream = new FileInputStream(firebaseKey);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .setDatabaseUrl("https://houseconrol-dkozak.firebaseio.com")
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
