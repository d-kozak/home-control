package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Sensor;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static io.dkozak.house.control.client.Utils.requireNonNegative;

public class SensorDetailsActivity extends SensorAwareActivity {

    public static final String SENSOR_NAME = "sensor_name";
    public static final String SENSOR_TYPE = "sensor_type";

    private int sensorId;

    private LineChartView chart;
    private Button statusButton;
    private TextView statusTxt;
    private TextView sensorNameTxt;
    private TextView sensorTypeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        setCurrentSensorId(sensorId);

        sensorNameTxt = findViewById(R.id.sensorName);
        sensorTypeTxt = findViewById(R.id.sensorType);

        statusButton = findViewById(R.id.statusButton);
        statusTxt = findViewById(R.id.statusTextView);


        chart = findViewById(R.id.chart);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sensor_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                removeSensor(sensorId, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(SensorDetailsActivity.this, "Sensor removed", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewSensorValues(Sensor currentSensor, SensorType sensorType) {
        sensorNameTxt.setText(currentSensor.getDescription());
        sensorTypeTxt.setText(sensorType.getName());

        List<List<Integer>> values = currentSensor.getValues();
        if (values.isEmpty()) {
            Log.e("Sensor details", "Empty sensor values");
            return;
        }
        List<Integer> lastValues = values.get(values.size() - 1);

        renderBooleanConfig(currentSensor, sensorType, lastValues);
        renderChart(values);
    }

    private void renderChart(List<List<Integer>> values) {
        List<AxisValue> xAxisValues = new ArrayList<>();
        List<AxisValue> yAxisValues = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();

        for (int i = 0; i < values.size() && i < 15; i++) {
            List<Integer> value = values.get(i);
            pointValues.add(new PointValue(i, value.get(0)));
            xAxisValues.add(new AxisValue(i));
            yAxisValues.add(new AxisValue(value.get(0)));
        }

        Line line = new Line(pointValues)
                .setColor(Color.parseColor("#9C27B0"));
        LineChartData data = new LineChartData(Arrays.asList(line));

        Axis xAxis = new Axis();
        xAxis.setValues(xAxisValues);
        xAxis.setTextSize(16);
        xAxis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(xAxis);

        Axis yAxis = new Axis();
        yAxis.setValues(yAxisValues);
        yAxis.setTextSize(16);
        xAxis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisYLeft(yAxis);

        chart.setLineChartData(data);
    }

    private void renderBooleanConfig(final Sensor currentSensor, SensorType sensorType, List<Integer> lastValues) {
        int booleanValueIndex = sensorType.getBoolValueIndex();
        if (booleanValueIndex != -1) {
            statusButton.setVisibility(View.VISIBLE);
            statusTxt.setVisibility(View.VISIBLE);

            if (booleanValueIndex >= lastValues.size()) {
                Log.e("Sensor details", "Could not extract last boolean at index " + booleanValueIndex + " value from " + lastValues);
                return;
            }
            boolean currentValue = false;
            switch (lastValues.get(booleanValueIndex)) {
                case 0:
                    currentValue = false;
                    break;
                case 1:
                    currentValue = true;
                    break;
                default:
                    Log.e("Sensor details", "Could not convert last value, should be 1 or 0, was " + lastValues.get(booleanValueIndex));
                    break;
            }

            final boolean currentValueFinal = currentValue;
            statusTxt.setText(currentValue ? "ON" : "OFF");
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorUpdateRequest(currentSensor, !currentValueFinal, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(SensorDetailsActivity.this, "Sensor updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } else {
            statusButton.setVisibility(View.GONE);
            statusTxt.setVisibility(View.GONE);
        }
    }



}
