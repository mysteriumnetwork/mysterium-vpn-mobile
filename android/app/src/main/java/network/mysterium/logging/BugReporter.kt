package network.mysterium.logging

import cat.ereza.logcatreporter.LogcatReporter
import com.crashlytics.android.Crashlytics
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class FeedbackException(message: String) : Exception(message)

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

    @ReactMethod
    fun sendFeedback(type: String, message: String) {
        Crashlytics.logException(FeedbackException(type + ":" + message))
    }
}
