import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.buildOscMessage
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class OscBoolTests {

    class BlobTestData : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            return Stream.of(
                Arguments.of(
                    "/", // input message pattern
                    arrayOf(true),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'T'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                Arguments.of(
                    "/foo", // input message pattern
                    arrayOf(true, true, false, true, false),
                    byteArrayOf(
                        '/'.code.toByte(), 'f'.code.toByte(), 'o'.code.toByte(), 'o'.code.toByte(),
                        0x00, 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'T'.code.toByte(), 'T'.code.toByte(), 'F'.code.toByte(),
                        'T'.code.toByte(), 'F'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BlobTestData::class)
    fun testBoolArgumentsInMessage(inputMessagePatern: String, inputArgs: Array<Boolean>, expectedMessageBytes: ByteArray) {
        // build message
        val message = buildOscMessage {
            addressPattern(inputMessagePatern)
            inputArgs.forEach { arg(it) }
        }
        // write to bytes
        val actualMessageBytes = writeToByteArray { message.write(it) }
        // compare
        assertEquals("\n${expectedMessageBytes.toHexDumpString()}", "\n${actualMessageBytes.toHexDumpString()}")
    }

}