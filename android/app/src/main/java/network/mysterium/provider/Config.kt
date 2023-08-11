package network.mysterium.provider

object Config {
    const val minMobileDataLimit = 50
    const val maxMobileDataLimit = 999999
    const val payPalRedirectUrl =
        "https://pilvytis.mysterium.network/api/v2/payment/paypal/redirect"
    const val stripeRedirectUrl =
        "https://pilvytis.mysterium.network/api/v2/payment/stripe/redirect"
    const val redirectUriReplacement = "tequilapi/auth"
    const val helpLink = "https://help.mystnodes.com"


    val deeplinkSSO = DeeplinkPath(
        scheme = DeeplinkPath.Scheme.SSO,
        queryPath = "?authorizationGrant=",
        parameterName = "authGrant"
    )
    val deeplinkClaim = DeeplinkPath(
        scheme = DeeplinkPath.Scheme.CLAIM,
        queryPath = "?mmnApiKey=",
        parameterName = "ApiKey"
    )
    val deepLinkOnboardingClicking = DeeplinkPath(
        scheme = DeeplinkPath.Scheme.CLICKBOARDING,
        queryPath = "?authorizationGrant=",
        parameterName = "authGrant"
    )
}

data class DeeplinkPath(
    val host: String = "mystnodes://",
    val scheme: Scheme,
    val queryPath: String?,
    val parameterName: String?
) {

    fun path(): String =
        host + scheme.scheme + queryPath?.let { "$queryPath{$parameterName}" }

    enum class Scheme(val scheme: String) {
        CLAIM("claim"),
        SSO("sso"),
        CLICKBOARDING("clickboarding"),
    }
}
