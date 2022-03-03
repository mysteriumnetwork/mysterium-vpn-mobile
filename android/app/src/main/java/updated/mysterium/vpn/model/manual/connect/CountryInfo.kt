package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import updated.mysterium.vpn.common.countries.Countries
import java.util.*

@Parcelize
data class CountryInfo(
    val countryFlagRes: Int? = null,
    val countryFlagBitmap: Bitmap? = null,
    val countryCode: String,
    val countryName: String,
    val proposalsNumber: Int = 0,
    var isSelected: Boolean = false
) : Parcelable {

    fun changeSelectionState() {
        isSelected = !isSelected
    }

}

fun Map<String, Int>.toCountryInfo(): List<CountryInfo> {
    val result = mutableListOf<CountryInfo>()
    this.mapKeys { item ->
        if (item.key.isNotBlank() && item.key.isNotEmpty() && item.value > 0) {
            val countryName = Countries
                .values.getOrDefault(item.key.toLowerCase(Locale.ROOT), null)?.name
            countryName?.let {
                result.add(
                    CountryInfo(
                        countryCode = item.key,
                        countryName = countryName,
                        countryFlagBitmap = Countries.bitmaps.getOrDefault(
                            item.key.toLowerCase(Locale.ROOT),
                            null
                        ),
                        proposalsNumber = item.value,
                        isSelected = false
                    )
                )
            }
        }
    }
    return result
}
