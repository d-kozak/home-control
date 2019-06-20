package io.dkozak.house.control.client.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    private String ruleId;

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

        ruleId = intent.getStringExtra(RULE_ID);
        if (ruleId != null) {
            setCurrentRuleId(ruleId);
        } else {
            onRuleDetails(new Rule(sensorId, 0, Comparison.EQ, 0, getDeviceId()));
        }

        confirmButton = findViewById(R.id.confirmBtn);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRule.setComparison((Comparison) comparisonSpinner.getSelectedItem());
                currentRule.setUserId(getUserId());
                currentRule.setThreshold(Integer.parseInt(thresholdInput.getText().toString()));
                currentRule.setOffset((int) indexSpinner.getSelectedItem());

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
    protected void onRuleDetails(Rule rule) {
        this.currentRule = rule;

        this.indexSpinner.setSelection(rule.getOffset());
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
