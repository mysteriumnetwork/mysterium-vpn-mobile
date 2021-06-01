package updated.mysterium.vpn.common.languages

import java.util.*

object LanguagesUtil {

    const val DEFAULT_LANGUAGE = "EN"
    private val languagesList = listOf(
        "EN",
        "AR",
        "RU",
        "TR",
        "PT",
        "ZH"
    )

    fun getUserDefaultLanguage() = Locale.getDefault().language.toUpperCase(Locale.ROOT)

    fun getCountryCodeByIndex(index: Int) = languagesList[index]

    fun getIndexByCountry(countryCode: String?) = if (countryCode == null) {
        languagesList.indexOf(DEFAULT_LANGUAGE)
    } else {
        languagesList.indexOf(countryCode)
    }
}
