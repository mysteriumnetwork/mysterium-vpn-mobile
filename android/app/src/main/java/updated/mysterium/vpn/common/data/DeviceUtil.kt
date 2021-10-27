package updated.mysterium.vpn.common.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
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
        val androidApi = Build.VERSION.SDK_INT
        return when {
            androidApi <= 24 -> "7.0"
            androidApi == 25 -> "7.1"
            androidApi == 26 -> "8.0"
            androidApi == 27 -> "8.1"
            androidApi == 28 -> "9"
            androidApi == 29 -> "10"
            androidApi == 30 -> "11"
            else -> "12"
        }
    }

    fun getConfiguredCountry(context: Context): String {
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return manager.networkCountryIso
    }
}
