package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import network.mysterium.vpn.R

class LongListPopUpWindow @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ListPopupWindow(context, attrs, defStyleAttr) {

    fun inflateView(adapter: ArrayAdapter<String>, spinnerAnchorView: View) {
        setAdapter(adapter)
        anchorView = spinnerAnchorView
        width = WRAP_CONTENT
        setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.drawable.spinner_settings_menu)
        )
        isModal = true
        promptPosition = POSITION_PROMPT_BELOW
        verticalOffset = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            context.resources.getDimension(R.dimen.margin_padding_size_micro),
            context.resources.displayMetrics
        ).toInt()
        setDropDownGravity(Gravity.CENTER)
    }
}
