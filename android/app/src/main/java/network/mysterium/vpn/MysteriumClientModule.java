package network.mysterium.vpn;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import mysterium.Mysterium;
import mysterium.MobileNetworkOptions;

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
        MobileNetworkOptions options = Mysterium.defaultNetworkOptions();
        options.setDiscoveryAPIAddress("https://devnet-api.mysterium.network/v1");
        try {
            Mysterium.newNode(getReactApplicationContext().getFilesDir().getCanonicalPath(), options);
            promise.resolve(0); // return 0 for successful start
        } catch (Exception e) {
            e.printStackTrace();
            promise.resolve(1); // return 1 for failed start
        }
    }
}
