package io.dkozak.house.control.client.view.sensorlist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.dkozak.house.control.client.R;

public class SensorViewHolder extends RecyclerView.ViewHolder {

    public final TextView sensorName;
    public final TextView sensorValue;

    public SensorViewHolder(@NonNull View itemView) {
        super(itemView);
        sensorName = itemView.findViewById(R.id.index);
        sensorValue = itemView.findViewById(R.id.sensorValue);
    }
}
