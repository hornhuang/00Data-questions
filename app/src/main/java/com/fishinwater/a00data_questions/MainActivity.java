package com.fishinwater.a00data_questions;

import androidx.appcompat.app.AppCompatActivity;

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
