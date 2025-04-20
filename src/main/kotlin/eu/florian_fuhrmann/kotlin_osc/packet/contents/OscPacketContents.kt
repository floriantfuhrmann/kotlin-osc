package eu.florian_fuhrmann.kotlin_osc.packet.contents

import java.io.OutputStream

/** Contents of an OSC packet (either a message or a bundle). */
interface OscPacketContents {

    /** The size of the message/bundle in bytes. */
    val size: Int

    /** Write the contents to the given output stream. */
    fun write(outputStream: OutputStream)

}