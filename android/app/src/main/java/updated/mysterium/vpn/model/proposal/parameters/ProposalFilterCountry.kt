package updated.mysterium.vpn.model.proposal.parameters

import android.graphics.Bitmap

data class ProposalFilterCountry(
    val code: String = "",
    val name: String = "",
    val flagImage: Bitmap? = null,
    val proposalsCount: Int = 0
)
