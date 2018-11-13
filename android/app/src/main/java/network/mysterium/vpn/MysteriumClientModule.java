package network.mysterium.vpn;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import network.mysterium.service.core.MysteriumAndroidCoreService;

public class MysteriumClientModule extends ReactContextBaseJavaModule {
    public MysteriumClientModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "MysteriumClientModule";
    }

    @ReactMethod
    public void startService(int port, Promise promise) {
        Log.i(getName(), "Will start node");
        try {
            String path = getReactApplicationContext().getFilesDir().getCanonicalPath();
            new MysteriumAndroidCoreService(path).startMobileNode();
            promise.resolve(0); // return 0 for successful start
        } catch (Exception e) {
            Log.e(getName(), "Starting service failed:", e);
            e.printStackTrace();
            promise.resolve(1); // return 1 for failed start
        }
    }
}
