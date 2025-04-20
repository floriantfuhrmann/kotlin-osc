package eu.florian_fuhrmann.kotlin_osc.packet

import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscPacketContents
import java.io.OutputStream

/**
 * An Osc packet containing either a [eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage] or a [OscBundle]
 */
class OscPacket(val contents: OscPacketContents) {

    /**
     * Write the contents size as osc int32 and the contents to the given
     * output stream. This format is only valid for TCP and OSC 1.0.
     */
    fun write(outputStream: OutputStream, prependSize: Boolean = true) {
        if (prependSize) {
            // write size as osc int32
            contents.size.asOscAtomic.write(outputStream)
        }
        // write packet contents
        contents.write(outputStream)
    }

}