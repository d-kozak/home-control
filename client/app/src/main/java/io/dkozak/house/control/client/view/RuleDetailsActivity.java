package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Collections;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.model.Comparison;
import io.dkozak.house.control.client.model.Rule;
import io.dkozak.house.control.client.model.SensorType;
import io.dkozak.house.control.client.model.SensorValue;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;

import static io.dkozak.house.control.client.Utils.requireNonNegative;

public class RuleDetailsActivity extends SensorAwareActivity {

    private Rule currentRule;

    private Spinner indexSpinner;
    private Spinner comparisonSpinner;
    private TextView thresholdInput;

    private Button confirmButton;
    private String ruleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        indexSpinner = findViewById(R.id.indexSpinner);

        comparisonSpinner = findViewById(R.id.comparisonSpinner);
        thresholdInput = findViewById(R.id.thresholdInput);

        Intent intent = getIntent();

        int sensorId = requireNonNegative(intent.getIntExtra(SENSOR_ID, -1));
        setCurrentSensorId(sensorId);
        int sensorTypeId = requireNonNegative(intent.getIntExtra(SENSOR_TYPE, -1));
        setCurrentSensorType(sensorTypeId);

        ruleId = intent.getStringExtra(RULE_ID);
        if (ruleId != null) {
            setCurrentRuleId(ruleId);
        } else {
            getSensorType(new Rule(sensorId, 0, Comparison.EQ, 0, getUserId()), sensorTypeId);
        }

        confirmButton = findViewById(R.id.confirmBtn);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRule.setComparison((Comparison) comparisonSpinner.getSelectedItem());
                currentRule.setUserId(getUserId());
                currentRule.setThreshold(Integer.parseInt(thresholdInput.getText().toString()));
                currentRule.setOffset(indexSpinner.getSelectedItemPosition());

                saveRule(currentRule, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(RuleDetailsActivity.this, "Rule saved", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onRuleDetails(Rule rule, final SensorType sensorType) {
        this.currentRule = rule;

        ArrayAdapter<SensorValue> indexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sensorType.getValueTypes());
        indexSpinner.setAdapter(indexAdapter);
        this.indexSpinner.setSelection(rule.getOffset());
        indexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SensorValue sensorValue = sensorType.getValueTypes().get(indexSpinner.getSelectedItemPosition());
                switch (sensorValue.getType()) {
                    case INT: {
                        ArrayAdapter<Comparison> comparisonAdapter = new ArrayAdapter<>(RuleDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item, Comparison.values());
                        comparisonSpinner.setAdapter(comparisonAdapter);
                    }
                    break;
                    case BOOL: {
                        ArrayAdapter<Comparison> comparisonAdapter = new ArrayAdapter<>(RuleDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item, Collections.singletonList(Comparison.EQ));
                        comparisonSpinner.setAdapter(comparisonAdapter);
                    }
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<Comparison> comparisonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Comparison.values());
        comparisonSpinner.setAdapter(comparisonAdapter);
        this.comparisonSpinner.setSelection(rule.getComparison().ordinal());

        this.thresholdInput.setText(rule.getThreshold() + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (ruleId != null) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.sensor_details_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (ruleId != null) {
                    removeRule(ruleId, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(RuleDetailsActivity.this, "Rule removed", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
