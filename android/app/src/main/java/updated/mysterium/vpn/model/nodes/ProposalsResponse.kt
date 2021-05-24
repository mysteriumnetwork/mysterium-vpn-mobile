package updated.mysterium.vpn.model.nodes

import com.beust.klaxon.Json

class ProposalsResponse(
    @Json(name = "proposals")
    val proposals: List<ProposalItem>?
)
