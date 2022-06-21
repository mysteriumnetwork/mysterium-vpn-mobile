package updated.mysterium.vpn.model.identity

enum class MigrateHermesStatus(val status: String) {
    REQUIRED("required"),
    FINISHED("finished");

    companion object {
        fun from(response: MigrateHermesStatusResponse?): MigrateHermesStatus? {
            return values().find { it.status == response?.status }
        }
    }
}
