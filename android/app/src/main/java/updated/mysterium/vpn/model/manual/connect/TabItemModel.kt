package updated.mysterium.vpn.model.manual.connect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TabItemModel(
    @StringRes val textResId: Int,
    @DrawableRes val selectedBackgroundResId: Int,
    @DrawableRes val unselectedBackgroundResId: Int
)
