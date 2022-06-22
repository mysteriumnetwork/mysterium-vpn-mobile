package updated.mysterium.vpn.model.nodes

import com.squareup.moshi.Json

class ProposalsResponse(
    @Json(name = "proposals")
    val proposals: List<ProposalItem>?
)
