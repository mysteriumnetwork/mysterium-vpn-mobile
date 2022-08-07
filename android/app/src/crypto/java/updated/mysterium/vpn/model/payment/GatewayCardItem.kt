package updated.mysterium.vpn.model.payment

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import network.mysterium.vpn.R

data class GatewayCardItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int
) {
    companion object {
        fun from(value: Gateway): GatewayCardItem? {
            return when (value) {
                Gateway.COINGATE -> GatewayCardItem(
                    R.string.crypto,
                    R.drawable.icon_crypto_payment_option
                )
                Gateway.PAYPAL -> GatewayCardItem(
                    R.string.paypal,
                    R.drawable.icon_paypal_payment_option
                )
                Gateway.STRIPE -> GatewayCardItem(
                    R.string.credit_card,
                    R.drawable.icon_stripe_payment_option
                )
                else -> null
            }
        }
    }
}
