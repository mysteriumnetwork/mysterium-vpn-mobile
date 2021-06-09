package updated.mysterium.vpn.common.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}
