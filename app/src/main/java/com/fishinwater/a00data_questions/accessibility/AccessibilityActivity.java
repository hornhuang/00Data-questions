package com.fishinwater.a00data_questions.accessibility;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fishinwater.a00data_questions.R;

import java.util.Objects;

/**
 * @author _yuanhao
 * @data 2020.3.31
 */
public class AccessibilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        init();
    }

    private void init() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.accessibility_service);
    }

    /**
     * 启动方法，规范代码
     * @param context
     */
    public static void actionStart(Activity context) {
        Intent intent = new Intent(context, AccessibilityActivity.class);
        context.startActivity(intent);
    }

}
