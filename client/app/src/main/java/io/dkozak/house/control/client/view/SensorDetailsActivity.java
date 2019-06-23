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
import android.widget.EditText;
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

    private int sensorId;

    private LineChartView chart;
    private Button statusButton;
    private TextView statusTxt;
    private TextView sensorNameTxt;
    private TextView sensorTypeTxt;
    private TextView sensorIdTxt;

    private TextView intValueName;
    private EditText intValue;
    private Button intValueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        setCurrentSensorId(sensorId);

        final int sensorTypeId = requireNonNegative(intent.getIntExtra(SENSOR_TYPE, -1));
        setCurrentSensorType(sensorTypeId);

        sensorNameTxt = findViewById(R.id.index);
        sensorTypeTxt = findViewById(R.id.sensorType);

        statusButton = findViewById(R.id.statusButton);
        statusTxt = findViewById(R.id.statusTextView);

        sensorIdTxt = findViewById(R.id.sensorId);

        intValueName = findViewById(R.id.intValueName);
        intValue = findViewById(R.id.intValue);
        intValueBtn = findViewById(R.id.intValueBtn);

        Button rulesBtn = findViewById(R.id.rulesBtn);
        rulesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SensorDetailsActivity.this, RuleListActivity.class);
                startActivity(intent.putExtra(SENSOR_ID, sensorId)
                        .putExtra(SENSOR_TYPE, sensorTypeId));
            }
        });


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
        sensorIdTxt.setText("Id: " + currentSensor.getSensorId());

        List<List<Integer>> values = currentSensor.getValues();
        if (values.isEmpty()) {
            Log.e("Sensor details", "Empty sensor values");
            return;
        }
        List<Integer> lastValues = values.get(values.size() - 1);

        renderIntConfig(currentSensor, sensorType, lastValues);
        renderBooleanConfig(currentSensor, sensorType, lastValues);
        renderChart(values);
    }

    private void renderChart(List<List<Integer>> values) {
        List<AxisValue> xAxisValues = new ArrayList<>();
        List<AxisValue> yAxisValues = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();

        int start = values.size() <= 15 ? 0 : values.size() - 15;
        for (int i = start; i < values.size(); i++) {
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
        chart.refreshDrawableState();
    }

    private void renderIntConfig(final Sensor currentSensor, SensorType sensorType, List<Integer> lastValues) {
        final int intValueIndex = sensorType.getIntValueIndex();
        if (intValueIndex != -1) {
            intValueName.setVisibility(View.VISIBLE);
            intValue.setVisibility(View.VISIBLE);
            intValueBtn.setVisibility(View.VISIBLE);

            if (intValueIndex >= lastValues.size()) {
                Log.e("Sensor details", "Could not extract last int at index " + intValueIndex + " value from " + lastValues);
                return;
            }
            final int currentValue = lastValues.get(intValueIndex);
            intValueName.setText(sensorType.getValueTypes().get(intValueIndex).getName());
            if (intValue.getText().toString().isEmpty())
                intValue.setText(currentValue + "");
            intValueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newValue = Integer.parseInt(intValue.getText().toString());
                    sensorUpdateRequest(currentSensor, intValueIndex, newValue, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(SensorDetailsActivity.this, "Request sent", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } else {
            intValueName.setVisibility(View.GONE);
            intValue.setVisibility(View.GONE);
            intValueBtn.setVisibility(View.GONE);
        }
    }

    private void renderBooleanConfig(final Sensor currentSensor, SensorType sensorType, List<Integer> lastValues) {
        final int booleanValueIndex = sensorType.getBoolValueIndex();
        if (booleanValueIndex != -1) {
            statusButton.setVisibility(View.VISIBLE);
            statusTxt.setVisibility(View.VISIBLE);

            if (booleanValueIndex >= lastValues.size()) {
                Log.e("Sensor details", "Could not extract last boolean at index " + booleanValueIndex + " value from " + lastValues);
                return;
            }
            final int currentValue = lastValues.get(booleanValueIndex);
            statusTxt.setText(currentValue == 1 ? "ON" : "OFF");
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorUpdateRequest(currentSensor, booleanValueIndex, currentValue == 1 ? 0 : 1, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(SensorDetailsActivity.this, "Request sent", Toast.LENGTH_LONG).show();
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
