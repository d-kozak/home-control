package io.dkozak.house.control.client.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;

public class SensorListActivity extends SensorAwareActivity {


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


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onNewSensorTypes(Map<Integer, SensorType> sensorTypes) {
        adapter.update(sensorTypes);
    }

    @Override
    protected void onNewUserSensors(List<Sensor> sensors) {
        adapter.update(sensors);
    }
}
