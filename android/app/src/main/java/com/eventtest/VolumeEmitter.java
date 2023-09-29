package com.eventtest;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.ReactPackage;
import android.content.BroadcastReceiver;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VolumeEmitter extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;

    public VolumeEmitter(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addLifecycleEventListener(this);
    }

    // Initialize and register BroadcastReceiver
    private final BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                int volume = (Integer) intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
                int prevVolume = (Integer) intent.getExtras().get("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE");

                String direction = volume > prevVolume ? "Volume Up" : "Volume Down";
                emitEventToReactNative("VolumeChangeEvent", direction);
            }
        }
    };

    @Override
    public void onHostResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        this.reactContext.registerReceiver(volumeReceiver, filter);
    }

    @Override
    public void onHostPause() {
        this.reactContext.unregisterReceiver(volumeReceiver);
    }

    @Override
    public void onHostDestroy() {
    }

    @ReactMethod
    public void addListener(String eventName) {
    }

    @ReactMethod
    public void removeListeners(Integer count) {
    }

    private void emitEventToReactNative(String eventName, String direction) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, direction);
    }

    @Override
    public String getName() {
        return "VolumeEmitter";
    }

    public static class VolumeEmitterPackage implements ReactPackage {
        @Override
        public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
            List<NativeModule> modules = new ArrayList<>();
            modules.add(new VolumeEmitter(reactContext));
            return modules;
        }

        @Override
        public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
            return Collections.emptyList();
        }
    }
}
