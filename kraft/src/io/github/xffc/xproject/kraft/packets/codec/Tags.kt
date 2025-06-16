package io.github.xffc.xproject.kraft.packets.codec

import io.github.xffc.xproject.kraft.packets.util.NumberType
import io.github.xffc.xproject.kraft.readMCString
import io.github.xffc.xproject.kraft.readVarInt
import io.github.xffc.xproject.kraft.readVarLong
import io.github.xffc.xproject.kraft.writeMCString
import io.github.xffc.xproject.kraft.writeVarInt
import io.github.xffc.xproject.kraft.writeVarLong
import kotlinx.io.Buffer
import kotlinx.io.readUInt
import kotlinx.io.readULong
import kotlinx.io.readUShort
import kotlinx.io.writeUInt
import kotlinx.io.writeULong
import kotlinx.io.writeUShort
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

@Suppress("UNUSED")
sealed class Tag<T : Annotation> {
    companion object {
        internal fun KClass<*>.findTypeParameter(predict: (KClass<*>) -> Boolean): Boolean =
            supertypes.any { it.arguments.any { arg -> predict(arg.type?.jvmErasure ?: return@any false) } }

        internal val annotations = Codec::class.nestedClasses.filter {
            it.isSubclassOf(Annotation::class)
        }.associateWith { annotation ->
            Tag::class.sealedSubclasses.first { sealed -> sealed.findTypeParameter { it == annotation } }
        }

        internal inline fun <reified T> Any.expectAs(): T =
            this as? T ?: throw IllegalArgumentException("Expected $this (${this::class}) to be ${T::class}")
    }

    internal abstract val annotation: T

    internal abstract fun KProperty<*>.encode(buffer: Buffer, value: Any)
    internal abstract fun KProperty<*>.decode(buffer: Buffer): Any

    class Text(override val annotation: Codec.Text) : Tag<Codec.Text>() {
        val maxLength = annotation.maxLength
        val isJson = annotation.isJson

        override fun KProperty<*>.encode(buffer: Buffer, value: Any) {
            val output = when (value) {
                is String -> value
                else -> {
                    if (!isJson) value.expectAs<String>() // will throw an error, which is expected

                    Json.Default.encodeToString(
                        serializer(returnType),
                        value
                    )
                }
            }

            buffer.writeMCString(output)
        }

        override fun KProperty<*>.decode(buffer: Buffer): Any {
            var value: Any = buffer.readMCString()

            if (isJson) value = Json.Default.decodeFromString(
                serializer(returnType),
                value as String
            )!!

            return value
        }
    }

    class Int(override val annotation: Codec.Integer) : Tag<Codec.Integer>() {
        val type = annotation.type
        val isEnum = annotation.isEnum

        override fun KProperty<*>.encode(buffer: Buffer, value: Any) {
            var outputValue: Any = value

            if (isEnum) {
                val element: Enum<*> = value.expectAs()

                outputValue = element.declaringJavaClass
                    .getField(element.name)
                    .getAnnotation(Ordinal::class.java)
                    .ordinal
            }

            when (type) {
                NumberType.DEFAULT -> buffer.writeInt(outputValue.expectAs())
                NumberType.VAR -> buffer.writeVarInt(outputValue.expectAs())
                NumberType.UNSIGNED -> buffer.writeUInt(outputValue.expectAs())
            }
        }

        override fun KProperty<*>.decode(buffer: Buffer): Any {
            var value: Any = when (type) {
                NumberType.DEFAULT -> buffer.readInt()
                NumberType.VAR -> buffer.readVarInt()
                NumberType.UNSIGNED -> buffer.readUInt()
            }

            if (isEnum) {
                val enumClass = (this.returnType.classifier as KClass<*>).java
                val element = enumClass.fields.first {
                    it.getAnnotation(Ordinal::class.java).ordinal == value.expectAs()
                }

                value = enumClass.enumConstants.first { (it as Enum<*>).name == element.name }
            }

            return value
        }

        @Target(AnnotationTarget.PROPERTY)
        annotation class Ordinal(val ordinal: kotlin.Int)
    }

    class Short(override val annotation: Codec.Short) : Tag<Codec.Short>() {
        val type = annotation.type

        override fun KProperty<*>.encode(buffer: Buffer, value: Any) = when (type) {
            NumberType.DEFAULT -> buffer.writeShort(value.expectAs())
            NumberType.VAR -> throw IllegalArgumentException("Short can't be encoded with type $type")
            NumberType.UNSIGNED -> buffer.writeUShort(value.expectAs())
        }

        override fun KProperty<*>.decode(buffer: Buffer): Any = when (type) {
            NumberType.DEFAULT -> buffer.readShort()
            NumberType.VAR -> throw IllegalArgumentException("Short can't be decoded with type $type")
            NumberType.UNSIGNED -> buffer.readUShort()
        }
    }

    class Long(override val annotation: Codec.Long) : Tag<Codec.Long>() {
        val type = annotation.type

        override fun KProperty<*>.encode(buffer: Buffer, value: Any) {
            when (type) {
                NumberType.DEFAULT -> buffer.writeLong(value.expectAs())
                NumberType.VAR -> buffer.writeVarLong(value.expectAs())
                NumberType.UNSIGNED -> buffer.writeULong(value.expectAs())
            }
        }

        override fun KProperty<*>.decode(buffer: Buffer): Any = when (type) {
            NumberType.DEFAULT -> buffer.readLong()
            NumberType.VAR -> buffer.readVarLong()
            NumberType.UNSIGNED -> buffer.readULong()
        }
    }
}