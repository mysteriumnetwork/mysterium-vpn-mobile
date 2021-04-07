package updated.mysterium.vpn.common.countries

import updated.mysterium.vpn.model.settings.ResidentCountry
import java.util.Locale

class CountriesUtil {

    fun getAllCountries(): List<ResidentCountry> {
        val countries = mutableListOf<ResidentCountry>()
        val isoCountries = Locale.getISOCountries()
        for (country in isoCountries) {
            val locale = Locale("en", country)
            val code = locale.country
            val name = locale.getDisplayCountry(Locale.ENGLISH) // Replace for current selected language
            if (code.isNotEmpty() && name.isNotEmpty()) {
                countries.add(ResidentCountry(name, code))
            }
        }
        return countries.sortedBy { it.fullName }
    }
}
