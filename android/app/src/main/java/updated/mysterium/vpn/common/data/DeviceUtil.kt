package updated.mysterium.vpn.common.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings

object DeviceUtil {

    @SuppressLint("HardwareIds")
    fun getDeviceID(contentResolver: ContentResolver) = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}
