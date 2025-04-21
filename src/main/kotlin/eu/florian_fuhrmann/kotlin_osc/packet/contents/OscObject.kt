package eu.florian_fuhrmann.kotlin_osc.packet.contents

import eu.florian_fuhrmann.kotlin_osc.packet.OscPacket
import java.io.OutputStream

/** Contents of an OSC packet or an osc bundle element (either a message or a bundle). */
interface OscObject {

    /** The size of the message/bundle in bytes. */
    val size: Int

    /** Write the contents to the given output stream. */
    fun write(outputStream: OutputStream)

    fun toOscPacket() = OscPacket(this)

}