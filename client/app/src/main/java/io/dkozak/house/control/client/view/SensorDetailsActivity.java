package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.dkozak.house.control.client.R;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static io.dkozak.house.control.client.Utils.requireNonNegative;
import static java.util.Objects.requireNonNull;

public class SensorDetailsActivity extends AppCompatActivity {

    public static final String SENSOR_NAME = "sensor_name";
    public static final String SENSOR_ID = "sensor_id";
    public static final String SENSOR_TYPE = "sensor_type";

    private int sensorId;
    private int sensorType;
    private ValueEventListener sensorDetailsListener;
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

    private void updateChart(List<List<Integer>> values) {

        List<PointValue> pointValues = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            List<Integer> value = values.get(i);
            pointValues.add(new PointValue(i, value.get(0)));
        }

        chart.setLineChartData(new LineChartData(Arrays.asList(new Line(pointValues))));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorDetailsListener = FirebaseDatabase.getInstance().getReference("sensor/" + sensorId + "/values")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<List<Integer>>> typeIndicator = new GenericTypeIndicator<List<List<Integer>>>() {
                        };
                        List<List<Integer>> values = dataSnapshot.getValue(typeIndicator);
                        if (values != null) {
                            Toast.makeText(SensorDetailsActivity.this, values.toString(), Toast.LENGTH_LONG).show();
                            updateChart(values);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorDetailsListener != null) {
            FirebaseDatabase.getInstance().getReference("sensor/" + sensorId + "/values")
                    .removeEventListener(sensorDetailsListener);
            sensorDetailsListener = null;
        }
    }
}
