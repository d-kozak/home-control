package io.dkozak.house.control.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import io.dkozak.house.control.client.model.SensorType;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    public static final String SENSOR_TYPES_PATH = "sensor-types";
    private ValueEventListener sensorTypeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                                requestSignIn();
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_LONG).show();
            setupDatabaseListeners();

            FirebaseDatabase.getInstance()
                    .getReference("fooo")
                    .push()
                    .setValue(42, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(MainActivity.this, "DONE!", Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            requestSignIn();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDatabaseListeners();
    }

    private void clearDatabaseListeners() {
        if (sensorTypeListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(SENSOR_TYPES_PATH)
                    .removeEventListener(sensorTypeListener);
            sensorTypeListener = null;
        }
    }

    private void setupDatabaseListeners() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        sensorTypeListener = database.getReference(SENSOR_TYPES_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Toast.makeText(MainActivity.this, "DATA!", Toast.LENGTH_LONG).show();
                        List<SensorType> sensorTypes = (List) dataSnapshot.getValue();
                        Toast.makeText(MainActivity.this, sensorTypes.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void requestSignIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "User " + user, Toast.LENGTH_LONG).show();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
