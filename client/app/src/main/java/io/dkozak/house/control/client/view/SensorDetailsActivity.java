package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static io.dkozak.house.control.client.Utils.requireNonNegative;
import static java.util.Objects.requireNonNull;

public class SensorDetailsActivity extends SensorAwareActivity {

    public static final String SENSOR_NAME = "sensor_name";
    public static final String SENSOR_TYPE = "sensor_type";

    private int sensorId;
    private int sensorType;
    private LineChartView chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        sensorType = requireNonNegative(intent.getIntExtra(SENSOR_TYPE, -1));
        String sensorName = requireNonNull(intent.getStringExtra(SENSOR_NAME));

        ((TextView) findViewById(R.id.sensorName)).setText(sensorName);

        chart = findViewById(R.id.chart);
    }

    @Override
    protected void onNewSensorValues(List<List<Integer>> values) {

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

}
