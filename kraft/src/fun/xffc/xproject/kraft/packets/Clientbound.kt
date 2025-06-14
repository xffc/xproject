package `fun`.xffc.xproject.kraft.packets

import `fun`.xffc.xproject.kraft.packets.codec.Codec
import `fun`.xffc.xproject.kraft.types.ServerStatus
import `fun`.xffc.xproject.kraft.types.State

class StatusResponse(
    @Codec.Text(32767, true)
    val status: ServerStatus
): Packet {
    companion object: Codec<StatusResponse>(0x00u, State.STATUS, StatusResponse::class), Clientbound
}

class PingResponse(
    @Codec.Long
    val timestamp: Long
): Packet {
    companion object: Codec<PingResponse>(0x01u, State.STATUS, PingResponse::class), Clientbound
}