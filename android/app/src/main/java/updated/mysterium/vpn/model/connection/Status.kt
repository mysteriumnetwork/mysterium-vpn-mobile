package updated.mysterium.vpn.model.connection

import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.manual.connect.Proposal

class Status(
    val state: String,
    val proposal: Proposal? = null
) {

    constructor(statusResponse: StatusResponse) : this(
        state = statusResponse.state,
        proposal = statusResponse.proposal?.let {
            Proposal(NodeEntity(it))
        }
    )

}
