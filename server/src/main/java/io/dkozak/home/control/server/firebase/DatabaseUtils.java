package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.*;

import java.util.concurrent.atomic.AtomicReference;
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


    public static void loadAndUpdate(DatabaseReference databaseRef, Consumer<DataSnapshot> block) {
        var listenerRef = new AtomicReference<ValueEventListener>();
        listenerRef.set(databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (listenerRef.get() != null) {
                    databaseRef.removeEventListener(listenerRef.get());
                    listenerRef.set(null);
                    block.accept(snapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        }));
    }
}
