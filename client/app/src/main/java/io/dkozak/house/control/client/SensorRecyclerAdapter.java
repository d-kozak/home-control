package io.dkozak.house.control.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public class SensorRecyclerAdapter extends RecyclerView.Adapter<SensorViewHolder> {
    private List<Sensor> sensors;
    private Map<Integer, SensorType> sensorTypes;

    public SensorRecyclerAdapter(List<Sensor> sensors, Map<Integer, SensorType> sensorTypes) {
        this.sensors = sensors;
        this.sensorTypes = sensorTypes;
    }

    public void update(List<Sensor> sensors) {
        this.sensors = sensors;
        this.notifyDataSetChanged();
    }

    public void update(Map<Integer, SensorType> sensorTypes) {
        this.sensorTypes = sensorTypes;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item_view, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensors.get(position);
        SensorType sensorType = sensorTypes.get(sensor.getSensorType());
        holder.sensorName.setText(sensorType.getName() + " " + sensor.getSensorId());
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
