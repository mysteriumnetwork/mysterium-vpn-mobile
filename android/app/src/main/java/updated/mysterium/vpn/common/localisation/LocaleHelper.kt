package updated.mysterium.vpn.common.localisation

import android.content.Context
import updated.mysterium.vpn.common.languages.LanguagesUtil
import java.util.*

object LocaleHelper {

    private var defaultLanguage: String = LanguagesUtil.getUserDefaultLanguage()

    fun onAttach(
        context: Context,
        currentLanguage: String = defaultLanguage
    ) = setLocale(context, currentLanguage)

    fun setLocale(
        context: Context,
        language: String
    ) = updateResources(context, language)

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(configuration)
    }
}
