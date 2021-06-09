package updated.mysterium.vpn.model.nodes

import android.os.Parcelable
import com.beust.klaxon.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProposalPaymentMethod(
    @Json(name = "currency")
    val currency: String,

    @Json(name = "per_gib")
    val perGib: Double,

    @Json(name = "per_hour")
    val perHour: Double
) : Parcelable
