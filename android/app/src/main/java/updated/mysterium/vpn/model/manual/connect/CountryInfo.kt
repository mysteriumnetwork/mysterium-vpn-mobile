package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import updated.mysterium.vpn.common.location.Countries
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

    companion object {

        fun from(countryCode: String, proposalsNumber: Int): CountryInfo? {
            if (countryCode.isBlank() || proposalsNumber <= 0) {
                return null
            }

            val countryName = Countries
                .values
                .getOrDefault(countryCode.toLowerCase(Locale.ROOT), null)
                ?.name

            return countryName?.let {
                CountryInfo(
                    countryCode = countryCode,
                    countryName = countryName,
                    countryFlagBitmap = Countries.bitmaps.getOrDefault(
                        countryCode.toLowerCase(Locale.ROOT),
                        null
                    ),
                    proposalsNumber = proposalsNumber,
                    isSelected = false
                )
            }
        }
    }

    fun changeSelectionState() {
        isSelected = !isSelected
    }

}
