package updated.mysterium.vpn.exceptions

import android.content.Context
import network.mysterium.vpn.R

open class BaseNetworkException(val exception: Exception) : Exception() {

    companion object {
        fun fromException(exception: Exception): BaseNetworkException {
            return BaseNetworkException(handleException(exception))
        }

        fun fromErrorCode(errorCode: String, errorMessage: String): BaseNetworkException {
            return BaseNetworkException(handleFromErrorCode(errorCode, errorMessage))
        }

        private fun handleFromErrorCode(errorCode: String, errorMessage: String): Exception {
            return when (errorCode) {
                "InvalidProposal" -> ConnectInvalidProposalException(errorMessage)
                "InsufficientBalance" -> ConnectInsufficientBalanceException(errorMessage)
                else -> {
                    if (errorMessage == "connection already exists") {
                        throw ConnectAlreadyExistsException(errorMessage)
                    } else {
                        throw ConnectUnknownException(errorMessage)
                    }
                }
            }
        }

        private fun handleException(exception: Exception): Exception {
            return if (exception.message?.contains(NO_BALANCE_ERROR_MESSAGE) == true) {
                TopupNoAmountException()
            } else if (isBalanceLimitException(exception.message.toString())) {
                TopupBalanceLimitException(getBalanceLimit(exception.message.toString()))
            } else error(exception)
        }

        private fun isBalanceLimitException(exceptionMessage: String?): Boolean {
            return exceptionMessage?.let {
                Regex(BALANCE_LIMIT_ERROR_REGEX_PATTERN).containsMatchIn(it)
            } ?: false
        }

        private fun getBalanceLimit(exceptionMessage: String?): Double {
            return exceptionMessage?.let {
                Regex("\\d+\\.\\d{2}").find(it)?.value?.toDouble()
            } ?: 5.0
        }

        private val BALANCE_LIMIT_ERROR_REGEX_PATTERN =
            "You can only top-up if you have less than \\d+.\\d{2} MYST in balance"
        private const val NO_BALANCE_ERROR_MESSAGE = "Cannot provide more balance at this time"
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
