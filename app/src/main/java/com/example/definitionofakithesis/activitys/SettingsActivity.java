package com.example.definitionofakithesis.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.definitionofakithesis.FileWork;
import com.example.definitionofakithesis.R;
import com.example.definitionofakithesis.fragments.CoefficientsTableFragment;
import com.example.definitionofakithesis.fragments.StageTableFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.back_to_home_button).setOnClickListener(v -> {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        });
        findViewById(R.id.stages_view).setOnClickListener(v -> {
            StageTableFragment stageTableFragment = new StageTableFragment();
            stageTableFragment.show(getSupportFragmentManager(), "custom");
        });
        findViewById(R.id.coefficients_view).setOnClickListener(v -> {
            CoefficientsTableFragment coefficientsTableFragment = new CoefficientsTableFragment();
            coefficientsTableFragment.show(getSupportFragmentManager(), "custom");
        });
    }
}