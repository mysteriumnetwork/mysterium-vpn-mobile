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
        fun from(value: Gateway): GatewayCardItem? {
            return when (value) {
                Gateway.COINGATE -> GatewayCardItem(
                    R.string.crypto,
                    R.drawable.icon_crypto_payment_option,
                    R.drawable.shape_rectangle_crypto_rounded_10,
                    R.color.payment_method_crypto_text_color,
                    R.color.payment_method_crypto_icon_color
                )
                Gateway.STRIPE -> GatewayCardItem(
                    R.string.stripe,
                    R.drawable.icon_stripe_payment_option,
                    R.drawable.shape_rectangle_stripe_rounded_10,
                    R.color.payment_method_stripe_text_color,
                    R.color.payment_method_stripe_icon_color
                )
                else -> null
            }
        }
    }
}
