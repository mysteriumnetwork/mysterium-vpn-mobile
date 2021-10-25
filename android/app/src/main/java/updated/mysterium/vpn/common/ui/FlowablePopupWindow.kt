package updated.mysterium.vpn.common.ui

import android.transition.TransitionManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow

class FlowablePopupWindow(
    contentView: ViewGroup? = null,
    width: Int = LinearLayout.LayoutParams.WRAP_CONTENT,
    height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
) : PopupWindow(contentView, width, height) {

    var gravity = Gravity.CENTER
    var xOffset = 0
    var yOffset = 0

    init {
        contentView?.let {
            TransitionManager.beginDelayedTransition(it)
        }
        isTouchable = true
        isFocusable = true
    }

    fun show() {
        showAtLocation(contentView, gravity, xOffset, yOffset)
    }
}
