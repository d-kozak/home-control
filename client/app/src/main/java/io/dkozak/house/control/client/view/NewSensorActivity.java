package io.dkozak.house.control.client.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;
import io.dkozak.house.control.client.view.sensorlist.OnSensorClickedListener;
import io.dkozak.house.control.client.view.sensorlist.SensorRecyclerAdapter;

public class NewSensorActivity extends SensorAwareActivity {

    private SensorRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView sensorView = findViewById(R.id.sensorView);

        sensorView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensorRecyclerAdapter(new OnSensorClickedListener() {
            @Override
            public void onClick(Sensor sensor, SensorType sensorType) {
                addNewSensor(sensor, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(NewSensorActivity.this, "Sensor added", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(NewSensorActivity.this, "Could not add new sensor", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        sensorView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewSensorTypes(Map<Integer, SensorType> sensorTypes) {
        adapter.update(sensorTypes);
    }

    @Override
    protected void onNewNonUserSensors(List<Sensor> sensors) {
        adapter.update(sensors);
    }

}
