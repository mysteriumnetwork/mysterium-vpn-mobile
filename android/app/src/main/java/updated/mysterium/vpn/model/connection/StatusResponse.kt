package updated.mysterium.vpn.model.connection

import com.beust.klaxon.Json
import updated.mysterium.vpn.model.nodes.ProposalItem

class StatusResponse(
    @Json(name = "State")
    val state: String,
    @Json(name = "Proposal")
    val proposal: ProposalItem? = null
)
