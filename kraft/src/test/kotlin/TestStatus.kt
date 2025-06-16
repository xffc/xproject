import io.github.xffc.xproject.kraft.KraftClient
import io.github.xffc.xproject.kraft.packets.Handshake
import io.github.xffc.xproject.kraft.packets.StatusRequest
import io.github.xffc.xproject.kraft.packets.StatusResponse
import io.github.xffc.xproject.kraft.types.ServerStatus
import io.github.xffc.xproject.kraft.types.State
import io.ktor.network.sockets.InetSocketAddress
import kotlinx.coroutines.CompletableDeferred

suspend fun main() {
    val defer = CompletableDeferred<ServerStatus>()

    val client = KraftClient.create(
        InetSocketAddress("artgame.fun", 25565),
        Handshake(770, "artgame.fun", 25565u, State.STATUS),
        1024
    )

    client.onPacket<StatusResponse> {
        defer.complete(status)
    }

    client.sendPacket(StatusRequest())

    println(defer.await())

    client.socket.close()
}