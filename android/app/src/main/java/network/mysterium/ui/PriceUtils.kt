package network.mysterium.ui

import network.mysterium.service.core.ProposalPaymentMoney
import java.text.DecimalFormat

object PriceUtils {

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
