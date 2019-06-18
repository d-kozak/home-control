package io.dkozak.house.control.client.view.sensorlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;

public class SensorRecyclerAdapter extends RecyclerView.Adapter<SensorViewHolder> {
    private final OnSensorClickedListener onClickListener;
    private List<Sensor> sensors;
    private Map<Integer, SensorType> sensorTypes;

    public SensorRecyclerAdapter(OnSensorClickedListener onClickListener) {
        this(onClickListener, Collections.<Sensor>emptyList(), Collections.<Integer, SensorType>emptyMap());
    }

    public SensorRecyclerAdapter(OnSensorClickedListener onClickListener, List<Sensor> sensors, Map<Integer, SensorType> sensorTypes) {
        this.onClickListener = onClickListener;
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
        final Sensor sensor = sensors.get(position);
        final SensorType sensorType = sensorTypes.get(sensor.getSensorType());
        final String sensorName = sensorType.getName() + " " + sensor.getSensorId();
        holder.sensorName.setText(sensorName);
        holder.sensorValue.setText(
                sensor.getValues().isEmpty() ? "" : sensor.getValues().get(sensor.getValues().size() - 1).get(0).toString()
        );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(sensor, sensorType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
