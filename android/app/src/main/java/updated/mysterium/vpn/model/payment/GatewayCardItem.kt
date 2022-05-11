package updated.mysterium.vpn.model.payment

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import network.mysterium.vpn.R

data class GatewayCardItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
    @DrawableRes val backgroundId: Int,
    @ColorRes val textColorId: Int,
    @ColorRes val iconColorId: Int
) {
    companion object {
        fun from(value: Gateway): GatewayCardItem {
            return when (value) {
                Gateway.GOOGLE -> GatewayCardItem(
                    R.string.google_play_billing,
                    R.drawable.ic_google_payment_method,
                    R.drawable.shape_rectangle_light_pink_rounded_10,
                    R.color.payment_method_google_text_color,
                    R.color.payment_method_google_icon_color
                )
            }
        }
    }
}
