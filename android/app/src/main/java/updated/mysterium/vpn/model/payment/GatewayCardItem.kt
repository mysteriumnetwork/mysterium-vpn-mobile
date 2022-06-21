package updated.mysterium.vpn.model.payment

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import network.mysterium.vpn.R

data class GatewayCardItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
    @ColorRes val backgroundId: Int,
    @ColorRes val textColorId: Int,
    @ColorRes val iconColorId: Int
) {
    companion object {
        fun from(value: Gateway): GatewayCardItem? {
            return when (value) {
                Gateway.COINGATE -> GatewayCardItem(
                    R.string.crypto,
                    R.drawable.icon_crypto,
                    R.color.payment_method_crypto_background,
                    R.color.payment_method_crypto_text_color,
                    R.color.payment_method_crypto_icon_color
                )
                Gateway.PAYPAL -> GatewayCardItem(
                    R.string.paypal,
                    R.drawable.icon_paypal,
                    R.color.payment_method_paypal_background,
                    R.color.payment_method_paypal_text_color,
                    R.color.payment_method_paypal_icon_color
                )
                Gateway.STRIPE -> GatewayCardItem(
                    R.string.stripe,
                    R.drawable.icon_stripe,
                    R.color.payment_method_crypto_background,
                    R.color.payment_method_crypto_text_color,
                    R.color.payment_method_crypto_icon_color
                )
                else -> null
            }
        }
    }
}
