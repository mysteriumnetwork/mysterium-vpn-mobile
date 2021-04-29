package updated.mysterium.vpn.model.manual.connect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingTabItem(
    @StringRes val textResId: Int,
    @DrawableRes val selectedBackgroundResId: Int,
    @DrawableRes val unselectedBackgroundResId: Int,
    @DrawableRes val rtlSelectedBackgroundResId: Int,
    @DrawableRes val rtlUnselectedBackgroundResId: Int,
)
