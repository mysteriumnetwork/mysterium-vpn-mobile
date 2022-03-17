package updated.mysterium.vpn.model.connection

import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.Proposal

data class Status(
    val state: ConnectionState,
    val proposal: Proposal? = null
) {

    constructor(statusResponse: StatusResponse) : this(
        state = ConnectionState.from(statusResponse.state),
        proposal = statusResponse.proposal?.let {
            Proposal(NodeEntity(it))
        }
    )

}
