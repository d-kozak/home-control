package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Comparison;
import io.dkozak.house.control.client.model.Rule;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;

import static io.dkozak.house.control.client.Utils.requireNonNegative;

public class RuleDetailsActivity extends SensorAwareActivity {

    private Rule currentRule;

    private Spinner indexSpinner;
    private Spinner comparisonSpinner;
    private TextView thresholdInput;

    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        indexSpinner = findViewById(R.id.indexSpinner);
        ArrayAdapter<Integer> indexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(0, 1));
        indexSpinner.setAdapter(indexAdapter);
        comparisonSpinner = findViewById(R.id.comparisonSpinner);
        ArrayAdapter<Comparison> comparisonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Comparison.values());
        comparisonSpinner.setAdapter(comparisonAdapter);
        thresholdInput = findViewById(R.id.thresholdInput);

        Intent intent = getIntent();

        int sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        setCurrentSensorId(sensorId);

        final String ruleId = intent.getStringExtra(RULE_ID);
        if (ruleId != null) {
            Toast.makeText(this, "Editing rule " + ruleId, Toast.LENGTH_LONG).show();
            setCurrentRuleId(ruleId);
        } else {
            Toast.makeText(this, "Creating new rule", Toast.LENGTH_LONG).show();
            onRuleDetails(new Rule(sensorId, 0, Comparison.EQ, 0, getDeviceId()));
        }

        confirmButton = findViewById(R.id.confirmBtn);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRule.comparison = (Comparison) comparisonSpinner.getSelectedItem();
                currentRule.deviceId = getDeviceId();
                currentRule.threshold = Integer.parseInt(thresholdInput.getText().toString());
                currentRule.offset = (int) indexSpinner.getSelectedItem();

                Toast.makeText(RuleDetailsActivity.this, "About to create rule " + currentRule, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onRuleDetails(Rule rule) {
        this.currentRule = rule;

        this.indexSpinner.setSelection(rule.offset);
        this.comparisonSpinner.setSelection(rule.comparison.ordinal());
        this.thresholdInput.setText(rule.threshold + "");
    }
}
