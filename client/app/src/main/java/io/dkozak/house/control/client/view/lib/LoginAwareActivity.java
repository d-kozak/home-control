package io.dkozak.house.control.client.view.lib;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.dkozak.house.control.client.view.LoginActivity;

public class LoginAwareActivity extends AppCompatActivity {


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
