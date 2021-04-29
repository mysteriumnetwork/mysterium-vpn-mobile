package updated.mysterium.vpn.model.manual.connect

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PresetFilter(
    val filterId: Int,
    @DrawableRes val selectedResId: Int,
    @DrawableRes val unselectedResId: Int,
    val title: String? = null,
    var isSelected: Boolean = false
) : Parcelable {

    fun changeSelectionState() {
        isSelected = !isSelected
    }
}
