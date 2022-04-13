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
                Gateway.COINGATE -> GatewayCardItem(
                    R.string.crypto,
                    R.drawable.icon_crypto_payment_method,
                    R.drawable.shape_rectangle_gray_rounded_10,
                    R.color.payment_method_crypto_text_color,
                    R.color.payment_method_crypto_icon_color
                )
                Gateway.PLAY_BILLING -> GatewayCardItem(
                    R.string.credit_card,
                    R.drawable.ic_card_payment_method,
                    R.drawable.shape_rectangle_light_pink_rounded_10,
                    R.color.payment_method_card_text_color,
                    R.color.payment_method_card_icon_color
                )
                Gateway.PAYPAL -> GatewayCardItem(
                    R.string.paypal,
                    R.drawable.icon_paypal_payment_method,
                    R.drawable.shape_rectangle_blue_rounded_10,
                    R.color.payment_method_paypal_text_color,
                    R.color.payment_method_paypal_icon_color
                )
            }
        }
    }
}
