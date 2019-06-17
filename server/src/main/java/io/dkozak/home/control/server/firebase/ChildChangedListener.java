package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public abstract class ChildChangedListener implements ChildEventListener {

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

    }

    @Override
    public void onCancelled(DatabaseError error) {

    }
}
