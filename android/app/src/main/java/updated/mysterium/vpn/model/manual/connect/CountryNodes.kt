package updated.mysterium.vpn.model.manual.connect

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryNodes(
    val info: CountryInfo,
    val proposalList: List<Proposal>
) : Parcelable {

    fun changeSelectionState() {
        info.isSelected = !info.isSelected
    }
}
