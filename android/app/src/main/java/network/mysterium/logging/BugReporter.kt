package network.mysterium.logging

import cat.ereza.logcatreporter.LogcatReporter
import com.crashlytics.android.Crashlytics
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class BugReporter(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "BugReporter"
    }

    @ReactMethod
    fun logException(value: String) {
        LogcatReporter.reportExceptionWithLogcat(RuntimeException(value))
    }

    @ReactMethod
    fun setUserIdentifier(userIdentifier: String) {
        Crashlytics.setUserIdentifier(userIdentifier)
    }
}
