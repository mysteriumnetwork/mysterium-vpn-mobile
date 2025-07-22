package updated.mysterium.vpn.model.nodes

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
class ProposalPaymentMoney(
    @Json(name = "amount")
    val amount: Double,

    @Json(name = "currency")
    val currency: String
) : Parcelable
