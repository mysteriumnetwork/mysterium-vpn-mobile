package network.mysterium.logging

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class BugReporterPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
        return mutableListOf(
            BugReporter(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<*, *>> {
        return mutableListOf()
    }
}
