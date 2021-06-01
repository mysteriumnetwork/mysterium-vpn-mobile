package updated.mysterium.vpn.common.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(this@hideKeyboard.windowToken, 0)
    }
}
