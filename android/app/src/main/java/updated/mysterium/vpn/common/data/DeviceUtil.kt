package updated.mysterium.vpn.common.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R

object DeviceUtil {

    @SuppressLint("HardwareIds")
    fun getDeviceID(contentResolver: ContentResolver): String = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )

    fun getAppVersion(context: Context): String {
        val version = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        return context.getString(R.string.report_issue_app_version_template, version)
    }

    fun getAndroidVersion(): String {
        val androidVersion = Build.VERSION.SDK_INT
        return "API: $androidVersion"
    }

    fun getConfiguredCountry(context: Context) = context
        .resources
        .configuration
        .locales
        .get(0)
        .country
}
