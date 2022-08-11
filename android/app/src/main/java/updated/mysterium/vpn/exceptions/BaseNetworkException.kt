package updated.mysterium.vpn.exceptions

import android.content.Context
import network.mysterium.vpn.R

open class BaseNetworkException(val exception: Exception) : Exception() {

    companion object {
        fun fromException(exception: Exception): BaseNetworkException {
            return BaseNetworkException(ErrorHandler.getException(exception))
        }

        fun fromErrorCode(errorCode: String, errorMessage: String): BaseNetworkException {
            return BaseNetworkException(ErrorHandler.getException(errorCode, errorMessage))
        }
    }

    fun getMessage(context: Context): String {
        return when (val cause = cause) {
            is TopupBalanceLimitException -> context.getString(
                R.string.payment_balance_limit_text,
                cause.limit
            )
            else -> localizedMessage ?: context.getString(R.string.unknown_error)
        }
    }
}
