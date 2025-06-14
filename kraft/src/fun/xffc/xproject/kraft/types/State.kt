package `fun`.xffc.xproject.kraft.types

import `fun`.xffc.xproject.kraft.packets.codec.Tag

enum class State {
    @Tag.Int.Ordinal(0)
    HANDSHAKING,
    @Tag.Int.Ordinal(1)
    STATUS,
    @Tag.Int.Ordinal(2)
    LOGIN,
    @Tag.Int.Ordinal(3)
    TRANSFER
}