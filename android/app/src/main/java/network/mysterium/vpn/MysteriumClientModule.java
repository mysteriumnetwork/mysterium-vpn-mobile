package network.mysterium.vpn;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

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
        String appPath = getReactApplicationContext().getFilesDir().getPath();
        System.out.println("App PATH: " +  appPath);
        //Mysterium.newNode("/data/user/0/network.mysterium.vpn/files");
        Mysterium.newNode(appPath);
        promise.resolve(0); // return 0 for successful start
    }
}
