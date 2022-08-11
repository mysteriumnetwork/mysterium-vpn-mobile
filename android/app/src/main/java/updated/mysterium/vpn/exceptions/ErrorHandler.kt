package updated.mysterium.vpn.exceptions

object ErrorHandler {

    private const val INVALID = "InvalidProposal"
    private const val INSUFFICIENT = "InsufficientBalance"
    private const val CONNECTION_EXISTS = "connection already exists"
    private const val NO_BALANCE_ERROR_MESSAGE = "Cannot provide more balance at this time"

    private val BALANCE_LIMIT_ERROR_REGEX_PATTERN =
        "You can only top-up if you have less than \\d+.\\d{2} MYST in balance"

    fun getException(errorCode: String, errorMessage: String): Exception {
        return when {
            errorCode == INVALID -> ConnectInvalidProposalException(errorMessage)
            errorCode == INSUFFICIENT -> ConnectInsufficientBalanceException(errorMessage)
            errorMessage == CONNECTION_EXISTS -> ConnectAlreadyExistsException(errorMessage)
            else -> ConnectUnknownException(errorMessage)
        }
    }

    fun getException(exception: Exception): Exception {
        return if (exception.message?.contains(NO_BALANCE_ERROR_MESSAGE) == true) {
            TopupNoAmountException()
        } else if (isBalanceLimitException(exception.message.toString())) {
            TopupBalanceLimitException(getBalanceLimit(exception.message))
        } else error(exception)
    }

    private fun isBalanceLimitException(exceptionMessage: String): Boolean {
        return Regex(BALANCE_LIMIT_ERROR_REGEX_PATTERN).containsMatchIn(exceptionMessage)
    }

    private fun getBalanceLimit(exceptionMessage: String?): Double {
        return exceptionMessage?.let {
            Regex("\\d+\\.\\d{2}").find(it)?.value?.toDouble()
        } ?: 5.0
    }
}
