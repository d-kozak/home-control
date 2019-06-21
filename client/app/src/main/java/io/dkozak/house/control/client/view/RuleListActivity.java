package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Rule;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;
import io.dkozak.house.control.client.view.rulelist.RuleOnClickListener;
import io.dkozak.house.control.client.view.rulelist.RuleRecyclerAdapter;

import static io.dkozak.house.control.client.Utils.requireNonNegative;

public class RuleListActivity extends SensorAwareActivity {

    private RuleRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        final int sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        setCurrentSensorId(sensorId);

        final int sensorTypeId = requireNonNegative(intent.getIntExtra(SENSOR_TYPE, -1));
        setCurrentSensorType(sensorTypeId);


        RecyclerView ruleView = findViewById(R.id.ruleView);
        ruleView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RuleRecyclerAdapter(new RuleOnClickListener() {
            @Override
            public void onClick(Rule rule) {
                Intent intent = new Intent(RuleListActivity.this, RuleDetailsActivity.class);
                intent.putExtra(RULE_ID, rule.getId())
                        .putExtra(SENSOR_ID, sensorId)
                        .putExtra(SENSOR_TYPE, sensorTypeId);
                startActivity(intent);
            }
        });
        ruleView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RuleListActivity.this, RuleDetailsActivity.class)
                        .putExtra(SENSOR_ID, sensorId)
                        .putExtra(SENSOR_TYPE, sensorTypeId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewDeviceRules(List<Rule> deviceRules, SensorType sensorType) {
        adapter.update(deviceRules, sensorType);
    }
}
