package updated.mysterium.vpn.common

import android.text.InputFilter

object Filters {

    val password = InputFilter { source, start, end, _, _, _ ->
        source?.subSequence(start, end)?.replace(Regex("[ ]"), "")
    }

}
