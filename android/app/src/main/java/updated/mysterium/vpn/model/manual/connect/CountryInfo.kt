package updated.mysterium.vpn.model.manual.connect

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryInfo(
    val countryFlagRes: Int? = null,
    val countryCode: String,
    val countryName: String,
    var isSelected: Boolean = false
): Parcelable
