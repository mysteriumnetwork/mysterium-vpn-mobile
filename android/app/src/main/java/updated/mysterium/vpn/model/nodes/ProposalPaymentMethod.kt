package updated.mysterium.vpn.model.nodes

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
class ProposalPaymentMethod(
    @Json(name = "currency")
    val currency: String,

    @Json(name = "per_gib")
    val perGib: Double,

    @Json(name = "per_hour")
    val perHour: Double
) : Parcelable
