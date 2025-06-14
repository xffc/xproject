package `fun`.xffc.xproject.kraft.packets.codec

import `fun`.xffc.xproject.kraft.packets.Packet
import `fun`.xffc.xproject.kraft.packets.util.NumberType
import `fun`.xffc.xproject.kraft.types.State
import kotlinx.io.Buffer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

abstract class Codec<T : Packet>(
    val id: UByte,
    val state: State,
    private val kclass: KClass<T>
) {
    internal val constructor = kclass.primaryConstructor
        ?: throw IllegalArgumentException("No constructor for $kclass")

    // fix of kclass.memberProperties incorrect order
    internal val properties = run {
        val names = constructor.parameters.map { it.name }
        names.map { name -> kclass.memberProperties.first { it.name == name } }
    }

    internal fun KProperty<*>.getTag(): Tag<*> {
        annotations.forEach { annotation ->
            return Tag.annotations[annotation.annotationClass]?.primaryConstructor?.call(annotation) ?: return@forEach
        }

        throw IllegalArgumentException("No tag for property $this")
    }

    fun encodeToBuffer(buffer: Buffer, packet: T) =
        properties.forEach { property ->
            property.getTag().run {
                property.encode(buffer, property.getter.call(packet)!!)
            }
        }

    fun decodeFromBuffer(buffer: Buffer): T {
        val arguments = mutableSetOf<Any>()

        properties.forEach { property ->
            arguments.add(property.getTag().run {
                property.decode(buffer)
            })
        }

        return constructor.call(*arguments.toTypedArray())
    }

    @Target(AnnotationTarget.PROPERTY)
    annotation class Text(val maxLength: Int, val isJson: Boolean = false)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Integer(val type: NumberType = NumberType.DEFAULT, val isEnum: Boolean = false)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Short(val type: NumberType = NumberType.DEFAULT)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Long(val type: NumberType = NumberType.DEFAULT)
}
