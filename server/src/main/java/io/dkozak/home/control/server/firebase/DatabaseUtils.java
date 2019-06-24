package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.function.Consumer;

public class DatabaseUtils {

    public static ChildEventListener childAdded(Consumer<DataSnapshot> block) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                block.accept(snapshot);
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
        };
    }

}
