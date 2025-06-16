package io.github.xffc.xproject.kraft.packets.util

import kotlin.experimental.or

fun readVarInt(reader: () -> Byte): Int {
    var value = 0
    var shift = 0
    var byte: Byte
    do {
        byte = reader()
        value = value or ((byte.toInt() and 0x7F) shl shift)
        shift += 7
    } while (byte.toInt() and 0x80 != 0)
    return value
}

fun readVarLong(reader: () -> Byte): Long {
    var value = 0L
    var shift = 0
    var byte: Byte
    do {
        byte = reader()
        value = value or ((byte.toLong() and 0x7F) shl shift)
        shift += 7
    } while (byte.toInt() and 0x80 != 0)
    return value
}

/**
 * @return Count of bytes written
 */
fun writeVarInt(value: Int, writer: (Byte) -> Unit): Int {
    var value = value
    var count = 0
    do {
        val byte = (value and 0x7F).toByte() or if (value > 0x7F) 0x80.toByte() else 0
        writer(byte)
        value = value ushr 7
        count++
    } while (value != 0)
    return count
}

/**
 * @return Count of bytes written
 */
fun writeVarLong(value: Long, writer: (Byte) -> Unit): Int {
    var value = value
    var count = 0
    do {
        val byte = (value and 0x7F).toByte() or if (value > 0x7F) 0x80.toByte() else 0
        writer(byte)
        value = value ushr 7
        count++
    } while (value != 0L)
    return count
}