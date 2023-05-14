package network.mysterium.node.model

data class NodeRunnerService(
    val name: String,
    val status: Status
) {
    enum class Status(val rawValue: String) {
        NOT_RUNNING("NotRunning"),
        STARTING("Starting"),
        RUNNING("Running")
    }
}


