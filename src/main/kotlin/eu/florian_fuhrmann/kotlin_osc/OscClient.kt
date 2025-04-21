package eu.florian_fuhrmann.kotlin_osc

import eu.florian_fuhrmann.kotlin_osc.packet.OscPacket
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage
import java.net.Socket

/**
 * OSC Client.
 *
 * @param socket the socket to send the OSC packets to
 * @param oscVersion the OSC version to use. (see [OscPacket.write] for
 *    details)
 */
class OscClient(
    val socket: Socket,
    val oscVersion: OscVersion = OscVersion.Specification1_1
) {

    /** Wraps the [oscMessage] in an [OscPacket] and sends it. */
    fun sendMessage(oscMessage: OscMessage) = sendPacket(OscPacket(oscMessage))

    /** Sends the given [OscPacket] to the socket. */
    fun sendPacket(oscPacket: OscPacket) {
        // write the packet to socket output stream and flush it
        oscPacket.write(socket.outputStream, oscVersion)
        socket.outputStream.flush()
    }

}