package io.github.xffc.xproject.kraft.packets

import io.github.xffc.xproject.kraft.packets.codec.Codec
import io.github.xffc.xproject.kraft.packets.util.NumberType
import io.github.xffc.xproject.kraft.types.State

class Handshake(
    @Codec.Integer(NumberType.VAR)
    val protocolVersion: Int,
    @Codec.Text(255)
    val serverAddress: String,
    @Codec.Short(NumberType.UNSIGNED)
    val serverPort: UShort,
    @Codec.Integer(NumberType.VAR, isEnum = true)
    val intent: State,
): Packet {
    companion object: Codec<Handshake>(0x00u, State.HANDSHAKING, Handshake::class), Serverbound
}

class StatusRequest: Packet {
    companion object: Codec<StatusRequest>(0x00u, State.STATUS, StatusRequest::class), Serverbound
}

class PingRequest(
    @Codec.Long
    val timestamp: Long
): Packet {
    companion object: Codec<PingRequest>(0x01u, State.STATUS, PingRequest::class), Serverbound
}