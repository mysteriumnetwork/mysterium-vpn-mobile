package com.mysteriumvpn;

import android.util.Log;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.Map;
import java.util.HashMap;

public class MysteriumClientModule extends ReactContextBaseJavaModule {

    public MysteriumClientModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "MysteriumClientModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("foo", "bar");
        return constants;
    }

    @ReactMethod
    public void startService() {
        Log.d("MysteriumClientModule", "Starting some sort of service");
    }

    @ReactMethod
    public void multiplyBy2(int number, Promise promise)
    {
      number *= 2;
      promise.resolve(number);
    }
}
