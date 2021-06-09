package updated.mysterium.vpn.model.settings

import androidx.annotation.StringRes

data class DnsOption(
    @StringRes val translatableValueResId: Int,
    val backendValue: String
)
