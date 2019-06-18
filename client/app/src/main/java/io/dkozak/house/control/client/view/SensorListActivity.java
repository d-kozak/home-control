package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public class SensorListActivity extends AppCompatActivity {


    public static final String SENSOR_TYPES_PATH = "sensor-types";
    public static final String SENSOR_PATH = "sensor";
    private ValueEventListener sensorTypeListener;
    private ValueEventListener sensorListener;

    private SensorRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView sensorView = findViewById(R.id.sensorView);
        sensorView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensorRecyclerAdapter(this);
        sensorView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_LONG).show();
            setupDatabaseListeners();
        } else {
            Toast.makeText(this, "Please login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void setupDatabaseListeners() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        sensorTypeListener = database.getReference(SENSOR_TYPES_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<Integer, SensorType> sensorTypes = new HashMap<>();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            sensorTypes.put(Integer.parseInt(item.getKey()), item.getValue(SensorType.class));
                        }
                        Toast.makeText(SensorListActivity.this, sensorTypes.toString(), Toast.LENGTH_LONG).show();
                        SensorListActivity.this.adapter.update(sensorTypes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        sensorListener = database.getReference(SENSOR_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<Sensor>> typeIndicator = new GenericTypeIndicator<List<Sensor>>() {
                        };
                        List<Sensor> sensors = dataSnapshot.getValue(typeIndicator);
                        Toast.makeText(SensorListActivity.this, sensors.toString(), Toast.LENGTH_LONG).show();
                        SensorListActivity.this.adapter.update(sensors);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
        if (sensorListener != null) {
            FirebaseDatabase.getInstance()
                    .getReference(SENSOR_PATH)
                    .removeEventListener(sensorListener);
            sensorListener = null;
        }
    }


}
