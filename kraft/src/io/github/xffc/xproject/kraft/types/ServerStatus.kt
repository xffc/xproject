package io.github.xffc.xproject.kraft.types

import io.github.xffc.xproject.core.serializers.UUIDSerializer
import io.github.xffc.xproject.kraft.serializers.ComponentSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import net.kyori.adventure.text.Component
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ServerStatus(
    val version: Version,
    val players: Players? = null,
    val description: @Serializable(with = ComponentSerializer::class) Component,
    val favicon: String? = null,
    val enforcesSecureChat: Boolean? = null
) {
    @Serializable
    data class Version(val name: String = "Old", val protocol: Int)

    @Serializable
    data class Players(
        val max: Int, val online: Int, val sample: List<Player>
    ) {
        @Serializable
        data class Player(val name: String, val id: @Serializable(with = UUIDSerializer::class) UUID)
    }
}