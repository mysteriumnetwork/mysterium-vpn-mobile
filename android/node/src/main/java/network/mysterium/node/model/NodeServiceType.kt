package network.mysterium.node.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class NodeServiceType(
    @SerialName("Service")
    val name: String,

    @SerialName("State")
    val state: State,
) {

    @Serializable(with = StatusSerializer::class)
    enum class State(val raw: String) {
        NOT_RUNNING("NotRunning"),
        STARTING("Starting"),
        RUNNING("Running"),
        UNKNOWN("unknown")
    }

    object StatusSerializer : KSerializer<State> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "Status",
            PrimitiveKind.STRING
        )

        override fun deserialize(decoder: Decoder): State {
            return when (decoder.decodeString()) {
                State.NOT_RUNNING.raw -> State.NOT_RUNNING
                State.STARTING.raw -> State.STARTING
                State.RUNNING.raw -> State.RUNNING
                else -> State.UNKNOWN
            }
        }

        override fun serialize(encoder: Encoder, value: State) {
            encoder.encodeString(value.raw)
        }

    }
}


