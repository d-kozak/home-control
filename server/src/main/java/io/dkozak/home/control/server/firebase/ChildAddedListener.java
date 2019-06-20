package io.dkozak.home.control.server.firebase;

import com.google.firebase.database.DataSnapshot;

public abstract class ChildAddedListener extends ChildChangedListener {
    @Override
    public void onChildRemoved(DataSnapshot snapshot) {

    }
}
