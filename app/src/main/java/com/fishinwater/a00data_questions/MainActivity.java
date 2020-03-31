package com.fishinwater.a00data_questions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fishinwater.a00data_questions.accessibility.AccessibilityActivity;
import com.fishinwater.a00data_questions.showpic.ShowPictureActivity;

/**
 * @author _yuanhao
 * @data 2020.3.31
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mServiceView;
    private TextView mInterfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        verifyStoragePermissions();
    }
    // Storage Permissions
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void init() {
        mServiceView = findViewById(R.id.accessibility_service);
        mInterfaceView = findViewById(R.id.call_interface);

        mServiceView.setOnClickListener(this);
        mInterfaceView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accessibility_service:
                AccessibilityActivity.actionStart(this);
                break;

            case R.id.call_interface:
                ShowPictureActivity.actionStart(this);
                break;

            default:
                break;
        }
    }
}
