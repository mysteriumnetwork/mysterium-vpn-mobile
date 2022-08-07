package updated.mysterium.vpn.model.payment

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import network.mysterium.vpn.R

data class PaymentOptionCardItem(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int
) {
    companion object {
        fun from(value: PaymentOption): PaymentOptionCardItem? {
            return when (value) {
                PaymentOption.COINGATE -> PaymentOptionCardItem(
                    R.string.crypto,
                    R.drawable.icon_crypto_payment_option
                )
                PaymentOption.PAYPAL -> PaymentOptionCardItem(
                    R.string.paypal,
                    R.drawable.icon_paypal_payment_option
                )
                PaymentOption.STRIPE -> PaymentOptionCardItem(
                    R.string.credit_card,
                    R.drawable.icon_stripe_payment_option
                )
                PaymentOption.MYST_TOTAL -> PaymentOptionCardItem(
                    R.string.myst,
                    R.drawable.icon_myst_payment_option
                )
                PaymentOption.MYST_POLYGON -> PaymentOptionCardItem(
                    R.string.polygon,
                    R.drawable.icon_polygon_payment_option
                )
                PaymentOption.MYST_ETHEREUM -> PaymentOptionCardItem(
                    R.string.ethereum,
                    R.drawable.icon_ethereum_payment_option
                )
                else -> null
            }
        }
    }
}
