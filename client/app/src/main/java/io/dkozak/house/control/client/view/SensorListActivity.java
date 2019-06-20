package io.dkozak.house.control.client.view;

import android.content.Intent;
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
import io.dkozak.house.control.client.view.sensorlist.OnSensorClickedListener;
import io.dkozak.house.control.client.view.sensorlist.SensorRecyclerAdapter;

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
        adapter = new SensorRecyclerAdapter(new OnSensorClickedListener() {
            @Override
            public void onClick(Sensor sensor, SensorType sensorType) {
                Intent intent = new Intent(SensorListActivity.this, SensorDetailsActivity.class);
                intent.putExtra(SensorDetailsActivity.SENSOR_NAME, sensor.getDescription());
                intent.putExtra(SensorAwareActivity.SENSOR_ID, sensor.getSensorId());
                intent.putExtra(SensorDetailsActivity.SENSOR_TYPE, sensorType.getName());
                startActivity(intent);
            }
        });
        sensorView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SensorListActivity.this, NewSensorActivity.class));
            }
        });
    }

    @Override
    protected void onNewSensorTypes(Map<Integer, SensorType> sensorTypes) {
        adapter.update(sensorTypes);
    }

    @Override
    protected void onNewUserSensors(List<Sensor> userSensors) {
        adapter.update(userSensors);
    }
}
