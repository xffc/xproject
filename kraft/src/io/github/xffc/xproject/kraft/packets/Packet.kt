package io.github.xffc.xproject.kraft.packets

import io.github.xffc.xproject.kraft.packets.codec.Codec
import kotlin.reflect.full.companionObjectInstance

sealed interface Packet {
    companion object {
        internal val builtinRegistry = (Serverbound::class.sealedSubclasses + Clientbound::class.sealedSubclasses)
            .mapNotNull { it.objectInstance as? Codec<*> }

        val serverbound = builtinRegistry.filter { it is Serverbound }.toMutableList()
        val clientbound = builtinRegistry.filter { it is Clientbound }.toMutableList()
    }

    fun <T: Packet> getCodec(): Codec<T> =
        this::class.companionObjectInstance as Codec<T>
}

sealed interface Serverbound
sealed interface Clientbound