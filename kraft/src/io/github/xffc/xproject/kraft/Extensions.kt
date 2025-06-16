package io.github.xffc.xproject.kraft

import io.github.xffc.xproject.kraft.packets.util.readVarInt
import io.github.xffc.xproject.kraft.packets.util.readVarLong
import io.github.xffc.xproject.kraft.packets.util.writeVarInt
import io.github.xffc.xproject.kraft.packets.util.writeVarLong
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString

fun Source.readVarInt(): Int =
    readVarInt(::readByte)

fun Source.readVarLong(): Long =
    readVarLong(::readByte)

fun Source.readMCString(): String =
    readString(readVarInt().toLong())

/**
 * @return Count of bytes written
 */
fun Sink.writeVarInt(value: Int): Int =
    writeVarInt(value, ::writeByte)

/**
 * @return Count of bytes written
 */
fun Sink.writeVarLong(value: Long): Int =
    writeVarLong(value, ::writeByte)

/**
 * @return Count of bytes written
 */
fun Sink.writeMCString(value: String): Int =
    writeVarInt(value.length).also { writeString(value) }