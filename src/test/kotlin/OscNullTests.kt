import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.buildOscMessage
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class OscNullTests {

    class BlobTestData : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            return Stream.of(
                Arguments.of(
                    "/", // input message pattern
                    arrayOf<Unit?>(null),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'N'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                Arguments.of(
                    "/foo", // input message pattern
                    arrayOf<Unit?>(null, null, null, null, null),
                    byteArrayOf(
                        '/'.code.toByte(), 'f'.code.toByte(), 'o'.code.toByte(), 'o'.code.toByte(),
                        0x00, 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'N'.code.toByte(), 'N'.code.toByte(), 'N'.code.toByte(),
                        'N'.code.toByte(), 'N'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BlobTestData::class)
    fun testNullArgumentsInMessage(inputMessagePatern: String, inputArgs: Array<Unit?>, expectedMessageBytes: ByteArray) {
        // build message
        val message = buildOscMessage {
            addressPattern(inputMessagePatern)
            inputArgs.forEach { _ ->
                arg(OscAtomics.Null)
            }
        }
        // write to bytes
        val actualMessageBytes = writeToByteArray { message.write(it) }
        // compare
        assertEquals("\n${expectedMessageBytes.toHexDumpString()}", "\n${actualMessageBytes.toHexDumpString()}")
    }

}