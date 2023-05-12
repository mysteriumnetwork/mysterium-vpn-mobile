package network.mysterium.provider.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

fun Context.getActivity(): Activity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}