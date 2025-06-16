package io.github.xffc.xproject.kraft

import io.github.xffc.xproject.kraft.packets.Handshake
import io.github.xffc.xproject.kraft.packets.Packet
import io.github.xffc.xproject.kraft.types.State
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.SocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.readBuffer
import io.ktor.utils.io.writeBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.io.Buffer

class KraftClient private constructor(
    internal val socket: Socket,
    internal val chunkSize: Int,
    val state: State
) {
    internal val readChannel = socket.openReadChannel()
    internal val writeChannel = socket.openWriteChannel(autoFlush = true)

    private val _packetHandlers = MutableSharedFlow<Packet>(extraBufferCapacity = 10)
    val packetHandlers = _packetHandlers.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (!socket.isClosed) {
                receivePacket()?.also { packet ->
                    _packetHandlers.tryEmit(packet)
                }
            }
        }
    }

    inline fun <reified T : Packet> onPacket(crossinline action: suspend T.() -> Unit) =
        CoroutineScope(Dispatchers.Default).launch {
            packetHandlers
                .filterIsInstance<T>()
                .collect { packet ->
                    action.invoke(packet)
                }
        }

    suspend fun <T : Packet> sendPacket(packet: T) {
        val data = Buffer()

        val codec = packet.getCodec<T>()
        data.writeVarInt(codec.id.toInt())
        codec.encodeToBuffer(data, packet)

        val output = Buffer()
        output.writeVarInt(data.size.toInt())
        output.write(data, data.size)

        writeChannel.writeBuffer(output)
    }

    internal suspend fun receivePacket(): Packet? {
        var packetId = 0
        var packetLength = 0L

        val data = Buffer()
        var started = false

        while (!socket.isClosed) {
            val readSize = readChannel.availableForRead.coerceAtMost(chunkSize)
            if (readSize < 1) continue

            val buf = readChannel.readBuffer(readSize)

            if (!started) {
                packetLength = buf.readVarInt().toLong()
                val tempLength = buf.size
                packetId = buf.readVarInt()
                packetLength -= tempLength - buf.size

                started = true
            }

            data.write(buf, buf.size)

            if (data.size == packetLength) break
        }

        if (!started) return null

        return Packet.clientbound
            .find { it.id.toInt() == packetId && it.state == state }
            ?.decodeFromBuffer(data)
    }

    companion object {
        suspend fun create(
            address: SocketAddress,
            handshake: Handshake,
            chunkSize: Int
        ): KraftClient {
            val socket = aSocket(SelectorManager(Dispatchers.IO))
                .tcp().connect(address)

            val client = KraftClient(socket, chunkSize, handshake.intent)
            client.sendPacket(handshake)

            return client
        }
    }
}