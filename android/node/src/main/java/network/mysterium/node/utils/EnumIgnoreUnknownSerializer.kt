package network.mysterium.node.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


abstract class EnumIgnoreUnknownSerializer<T : Enum<T>>(values: Array<out T>, private val defaultValue: T) :
    KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(values.first()::class.qualifiedName!!, PrimitiveKind.STRING)

    private val lookup = values.associateBy({ it }, { it.serialName })
    private val revLookup = values.associateBy { it.serialName }

    private val Enum<T>.serialName: String
        get() = this::class.java.getField(this.name).getAnnotation(SerialName::class.java)?.value ?: name

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(lookup.getValue(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return revLookup[decoder.decodeString()] ?: defaultValue // map.getOrDefault is not available < API-24
    }
}

