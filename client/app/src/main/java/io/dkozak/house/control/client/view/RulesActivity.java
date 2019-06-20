package io.dkozak.house.control.client.view;

import android.os.Bundle;

import io.dkozak.house.control.client.R;
import io.dkozak.house.control.client.view.lib.SensorAwareActivity;

public class RulesActivity extends SensorAwareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
    }
}
