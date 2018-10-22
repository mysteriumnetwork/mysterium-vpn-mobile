package network.mysterium.vpn;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import mysterium.Mysterium;

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
        Mysterium.newNode();
        promise.resolve(0); // return 0 for successful start
    }
}
