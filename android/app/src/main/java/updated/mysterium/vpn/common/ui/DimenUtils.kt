package updated.mysterium.vpn.common.ui

import android.content.res.Resources

object DimenUtils {

    fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()
}
