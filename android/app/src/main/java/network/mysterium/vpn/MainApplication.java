package network.mysterium.vpn;

import android.app.Application;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.dieam.reactnativepushnotification.ReactNativePushNotificationPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import cat.ereza.logcatreporter.LogcatReporter;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import network.mysterium.logging.BugReporterPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
  private static final String TAG = "MainApplication";

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.asList(
              new MainReactPackage(),
              new ReactNativePushNotificationPackage(),
              new BugReporterPackage(),
              new VectorIconsPackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    Fabric.with(this, new Crashlytics());
    LogcatReporter.install();
    Crashlytics.setInt("android_sdk_int", android.os.Build.VERSION.SDK_INT);

    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);

    Log.i(TAG, "Application started");
  }
}
