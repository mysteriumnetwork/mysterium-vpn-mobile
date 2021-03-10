package updated.mysterium.vpn.model.manual.connect

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryNodes(
    val countryFlagRes: Int? = null,
    val countryCode: String,
    val countryName: String,
    val proposalList: List<Proposal>
) : Parcelable
