package updated.mysterium.vpn.common.extensions

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(this@hideKeyboard.windowToken, 0)
    }
}

fun EditText.setSelectionChangedListener(
    onSelectionChangedListener: (selectionEnd: Int) -> Unit
) {
    accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun sendAccessibilityEvent(host: View?, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                val editText = this@setSelectionChangedListener
                onSelectionChangedListener.invoke(editText.selectionEnd)
            }
        }
    }
}
