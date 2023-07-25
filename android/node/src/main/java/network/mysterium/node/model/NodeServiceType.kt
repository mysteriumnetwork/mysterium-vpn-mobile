package network.mysterium.node.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import network.mysterium.node.utils.EnumIgnoreUnknownSerializer

@Serializable
data class NodeServiceType(
    val id: Service,
    val state: State
) {

    @Serializable(with = StatusSerializer::class)
    enum class State(val raw: String) {
        NOT_RUNNING("NotRunning"),
        STARTING("Starting"),
        RUNNING("Running"),
        UNKNOWN("unknown")
    }

    @Serializable(with = ServiceSerializer::class)
    enum class Service(val order: Int) {
        @SerialName("wireguard")
        WIREGUARD(4),

        @SerialName("data_transfer")
        DATA_TRANSFER(2),

        @SerialName("scraping")
        SCRAPING(1),

        @SerialName("dvpn")
        DVPN(3),

        @SerialName("other")
        OTHER(Int.MAX_VALUE)
    }

    object ServiceSerializer : EnumIgnoreUnknownSerializer<Service>(Service.values(), Service.OTHER)

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


