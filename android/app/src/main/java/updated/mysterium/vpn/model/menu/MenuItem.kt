package updated.mysterium.vpn.model.menu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val subTitleResId: Int? = null,
    val isActive: Boolean = true
) {

    var onItemClickListener: (() -> Unit)? = null
    var dynamicSubtitle: String? = null
}
