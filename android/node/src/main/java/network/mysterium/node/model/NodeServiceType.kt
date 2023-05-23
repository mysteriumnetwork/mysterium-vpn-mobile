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
    enum class Service(val raw: String) {
        WIREGUARD("wireguard"),
        SCRAPING("scraping"),
        DATA_TRANSFER("data_transfer"),
        OTHER("other")
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

    object ServiceSerializer : KSerializer<Service> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            "Status",
            PrimitiveKind.STRING
        )

        override fun deserialize(decoder: Decoder): Service {
            return when (decoder.decodeString()) {
                Service.WIREGUARD.raw -> Service.WIREGUARD
                Service.SCRAPING.raw -> Service.SCRAPING
                Service.DATA_TRANSFER.raw -> Service.DATA_TRANSFER
                else -> Service.OTHER
            }
        }

        override fun serialize(encoder: Encoder, value: Service) {
            encoder.encodeString(value.raw)
        }
    }
}


