package eu.florian_fuhrmann.kotlin_osc.packet

import eu.florian_fuhrmann.kotlin_osc.OscVersion
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscPacketContents
import eu.florian_fuhrmann.kotlin_osc.utils.SlipOutputStream
import java.io.OutputStream

/**
 * An Osc packet containing either a [eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage] or a [OscBundle]
 */
class OscPacket(val contents: OscPacketContents) {

    /**
     * Write the contents of this packet to the given output stream. Prepends
     * size-count-preamble for Spec 1.0 and wraps the output in a double-ended
     * SLIP frame for Spec 1.1.
     *
     * @param outputStream the output stream to write to
     * @param oscVersion the OSC version to use for writing. Defaults to
     *    [OscVersion.Specification1_1]
     */
    fun write(outputStream: OutputStream, oscVersion: OscVersion = OscVersion.Specification1_1) {
        when (oscVersion) {
            OscVersion.Specification1_0 -> {
                // write size-count-preamble
                contents.size.asOscAtomic.write(outputStream)
                // write plain packet contents
                contents.write(outputStream)
            }

            OscVersion.Specification1_1 -> {
                // write packet contents (wrapping in SLIP)
                val slipOutputStream = SlipOutputStream(outputStream, useDoubleEnd = true)
                slipOutputStream.beginFrame()
                contents.write(slipOutputStream)
                slipOutputStream.endFrame()
            }
        }
    }

}