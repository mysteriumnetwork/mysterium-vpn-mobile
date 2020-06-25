package network.mysterium.ui

import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import java.text.DecimalFormat

object PriceUtils {
    fun pricePerMinute(pm: ProposalPaymentMethod?): ProposalPaymentMoney {
        if (pm == null || pm.rate.perSeconds == 0L) {
            var currency = ""
            if (pm != null) {
               currency = pm.price.currency
            }
            return ProposalPaymentMoney(amount = 0.0, currency = currency)
        }

        return ProposalPaymentMoney(
                amount = (60.0 / pm.rate.perSeconds) * pm.price.amount,
                currency = pm.price.currency
        )
    }

    val bytesInGiB = 1024 * 1024 * 1024

    fun pricePerGiB(pm: ProposalPaymentMethod?): ProposalPaymentMoney {
        if (pm == null || pm.rate.perSeconds == 0L) {
            var currency = ""
            if (pm != null) {
                currency = pm.price.currency
            }
            return ProposalPaymentMoney(amount = 0.0, currency = currency)
        }

        return ProposalPaymentMoney(
                amount = (bytesInGiB / pm.rate.perBytes) * pm.price.amount,
                currency = pm.price.currency
        )
    }

    fun displayMoney(m: ProposalPaymentMoney, opts: DisplayMoneyOptions = DisplayMoneyOptions()): String {
        var currency = ""
        if (opts.showCurrency) {
            currency = m.currency
        }
        if (m.currency == "MYST" || m.currency == "MYSTT") {
            val amount = m.amount / 100_000_000
            var formatDigits = ""
            for (i in 0..opts.fractionDigits) {
                formatDigits += "#"
            }
            val amountStr = DecimalFormat("#.$formatDigits").format(amount)
            return "${amountStr}${currency}"
        }
        return "${m.amount}${currency}"
    }
}

class DisplayMoneyOptions(
        val showCurrency: Boolean = false,
        val fractionDigits: Int = 6
)

/*

export interface DisplayMoneyOptions {
  showCurrency?: boolean
  fractionDigits?: number
  removeInsignificantZeros?: boolean
}


export const displayMoney = (
  m: Money,
  {
    showCurrency = false,
    fractionDigits = 6,
    removeInsignificantZeros = true,
  }: DisplayMoneyOptions = {}
): string => {
  if (m.currency == Currency.MYST || m.currency == Currency.MYSTTestToken) {
    let amount = m.amount ?? 0
    amount = amount / 100000000 // adjust
    let amountStr = amount.toFixed(fractionDigits) // fractions
    if (removeInsignificantZeros) {
      amountStr = Number(amountStr).toString()
    }
    return `${amountStr}${showCurrency ? m.currency : ''}`
  }
  return `${m.amount}${showCurrency ? m.currency : ''}`
}


export const pricePerMinute = (pm?: PaymentMethod): Money => {
  if (!pm || !pm.rate.perSeconds) {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return { amount: 0, currency: pm!.price.currency }
  }
  return {
    amount: Math.round((60 / pm.rate.perSeconds) * pm.price.amount),
    currency: pm.price.currency,
  }
}

const bytesInGiB = 1024 * 1024 * 1024

export const pricePerGiB = (pm?: PaymentMethod): Money => {
  if (!pm || !pm.rate.perBytes) {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return { amount: 0, currency: pm!.price.currency }
  }
  return {
    amount: Math.round((bytesInGiB / pm.rate.perBytes) * pm.price.amount),
    currency: pm.price.currency,
  }
}



export enum Currency {
  MYST = 'MYST',
  MYSTTestToken = 'MYSTT',
}

export interface DisplayMoneyOptions {
  showCurrency?: boolean
  fractionDigits?: number
  removeInsignificantZeros?: boolean
}

 */