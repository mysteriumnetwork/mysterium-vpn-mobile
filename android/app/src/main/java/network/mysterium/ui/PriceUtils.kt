package network.mysterium.ui

import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import java.text.DecimalFormat

object PriceUtils {
    fun pricePerMinute(pm: ProposalPaymentMethod?): ProposalPaymentMoney {
        val currency = pm?.price?.currency ?: ""

        if (pm == null || pm.rate.perSeconds == 0.0) {
            return ProposalPaymentMoney(amount = 0.0, currency = currency)
        }

        return ProposalPaymentMoney(
                amount = (60.0 / pm.rate.perSeconds) * pm.price.amount,
                currency = currency
        )
    }

    private const val bytesInGiB = 1024 * 1024 * 1024

    fun pricePerGiB(pm: ProposalPaymentMethod?): ProposalPaymentMoney {
        val currency = pm?.price?.currency ?: ""

        if (pm == null || pm.rate.perBytes == 0.0) {
            return ProposalPaymentMoney(amount = 0.0, currency = currency)
        }

        return ProposalPaymentMoney(
                amount = (bytesInGiB / pm.rate.perBytes) * pm.price.amount,
                currency = currency
        )
    }

    fun displayMoney(m: ProposalPaymentMoney, opts: DisplayMoneyOptions = DisplayMoneyOptions()): String {
        var currency = ""
        if (opts.showCurrency) {
            currency = m.currency
        }
        if (m.currency == "MYST" || m.currency == "MYSTT") {
            val amount = m.amount
            val formatDigits = "#".repeat(opts.fractionDigits)
            val amountStr = DecimalFormat("#.${formatDigits}").format(amount)
            return "${amountStr}${currency}"
        }
        return "${m.amount}${currency}"
    }
}

class DisplayMoneyOptions(
        val showCurrency: Boolean = false,
        val fractionDigits: Int = 6
)
