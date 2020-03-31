package com.fishinwater.a00data_questions.accessibility.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

/**
 * @author fishinwater-1999
 * @date :2020/3/31 10:11
 */
public class AccessibilityService extends android.accessibilityservice.AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}
