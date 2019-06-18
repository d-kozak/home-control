package io.dkozak.house.control.client;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SensorViewHolder extends RecyclerView.ViewHolder {

    public final TextView sensorName;
    public final TextView sensorValue;

    public SensorViewHolder(@NonNull View itemView) {
        super(itemView);
        sensorName = itemView.findViewById(R.id.sensorName);
        sensorValue = itemView.findViewById(R.id.sensorValue);
    }
}
