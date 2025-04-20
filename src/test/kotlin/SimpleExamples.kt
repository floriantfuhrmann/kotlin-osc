import eu.florian_fuhrmann.kotlin_osc.OscVersion
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.OscPacket
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleExamples {

    private val Char.b: Byte
        get() = this.code.toByte()

    // for spec 1.0
    val EXAMPLE_MESSAGE_1_EXPECTED = byteArrayOf(
        0x00, 0x00, 0x00, 0x20,
        '/'.b, 'o'.b, 's'.b, 'c'.b,
        'i'.b, 'l'.b, 'l'.b, 'a'.b,
        't'.b, 'o'.b, 'r'.b, '/'.b,
        '4'.b, '/'.b, 'f'.b, 'r'.b,
        'e'.b, 'q'.b, 'u'.b, 'e'.b,
        'n'.b, 'c'.b, 'y'.b, 0x00,
        ','.b, 'f'.b, 0x00, 0x00,
        0x43, 0xDC.toByte(), 0x00, 0x00
    )

    @Test
    fun example1() {
        // example message 1 from https://opensoundcontrol.stanford.edu/spec-1_0-examples.html#osc-message-examples
        val message = OscMessage("/oscillator/4/frequency", listOf(440.0f.asOscAtomic))
        // wrapped in a packet
        val packet = OscPacket(message)
        // get packet data
        val packetData = writeToByteArray { packet.write(it, oscVersion = OscVersion.Specification1_0) }
        // check if the packet data matches the expected data
        assertEquals("\n${EXAMPLE_MESSAGE_1_EXPECTED.toHexDumpString()}", "\n${packetData.toHexDumpString()}")
    }

}