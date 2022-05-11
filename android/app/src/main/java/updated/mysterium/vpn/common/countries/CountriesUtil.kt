package updated.mysterium.vpn.common.countries

import updated.mysterium.vpn.model.settings.CountryISO
import java.util.*

object CountriesUtil {

    fun getAllResidentCountries(): List<CountryISO> {
        val countries = mutableListOf<CountryISO>()
        val isoCountries = Locale.getISOCountries()
        for (country in isoCountries) {
            val locale = Locale("en", country)
            val code = locale.country
            val name =
                locale.getDisplayCountry(Locale.ENGLISH) // Replace for current selected language
            if (code.isNotEmpty() && name.isNotEmpty()) {
                countries.add(CountryISO(name, code))
            }
        }
        return countries.sortedBy { it.fullName }
    }

}
