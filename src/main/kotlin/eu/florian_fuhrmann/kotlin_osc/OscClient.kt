package eu.florian_fuhrmann.kotlin_osc

import eu.florian_fuhrmann.kotlin_osc.packet.OscPacket
import java.io.ByteArrayOutputStream
import java.net.Socket

class OscClient(val socket: Socket) {

    @OptIn(ExperimentalStdlibApi::class)
    fun sendPacket(oscPacket: OscPacket) {
        oscPacket.write(socket.outputStream)
    }

}