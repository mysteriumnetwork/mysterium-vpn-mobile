package updated.mysterium.vpn.model.proposal.parameters

enum class QualityLevel(val level: Int) {
    UNKNOWN(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        fun parse(level: Int): QualityLevel {
            return values().find { it.level == level } ?: UNKNOWN
        }
    }
}
