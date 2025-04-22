import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.buildOscMessage
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals

// Tests various no data atomics
class OscNoDataAtomicsTests {

    enum class NoDataAtomicTypeToTest {
        True, False, Null, Impulse
    }

    class BlobTestData : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            return Stream.of(
                // no args:
                Arguments.of(
                    "/", // input message pattern
                    arrayOf<NoDataAtomicTypeToTest>(),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 0x00, 0x00, 0x00
                    ) // expected message bytes
                ),
                // one arg:
                Arguments.of(
                    "/", // input message pattern
                    arrayOf(NoDataAtomicTypeToTest.True),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'T'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                Arguments.of(
                    "/", // input message pattern
                    arrayOf(NoDataAtomicTypeToTest.False),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'F'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                Arguments.of(
                    "/", // input message pattern
                    arrayOf(NoDataAtomicTypeToTest.Null),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'N'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                Arguments.of(
                    "/", // input message pattern
                    arrayOf(NoDataAtomicTypeToTest.Impulse),
                    byteArrayOf(
                        '/'.code.toByte(), 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'I'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
                // all args (every type at least once) in one message in random order:
                Arguments.of(
                    "/foo", // input message pattern
                    arrayOf(NoDataAtomicTypeToTest.Null, NoDataAtomicTypeToTest.True, NoDataAtomicTypeToTest.Impulse, NoDataAtomicTypeToTest.False, NoDataAtomicTypeToTest.Impulse),
                    byteArrayOf(
                        '/'.code.toByte(), 'f'.code.toByte(), 'o'.code.toByte(), 'o'.code.toByte(),
                        0x00, 0x00, 0x00, 0x00,
                        ','.code.toByte(), 'N'.code.toByte(), 'T'.code.toByte(), 'I'.code.toByte(),
                        'F'.code.toByte(), 'I'.code.toByte(), 0x00, 0x00
                    ) // expected message bytes
                ),
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BlobTestData::class)
    fun testNoDataArgumentsInMessage(inputMessagePatern: String, inputArgs: Array<NoDataAtomicTypeToTest>, expectedMessageBytes: ByteArray) {
        // build message
        val message = buildOscMessage {
            addressPattern(inputMessagePatern)
            inputArgs.forEach { it ->
                when(it) {
                    NoDataAtomicTypeToTest.True -> arg(OscAtomics.True)
                    NoDataAtomicTypeToTest.False -> arg(OscAtomics.False)
                    NoDataAtomicTypeToTest.Null -> arg(OscAtomics.Null)
                    NoDataAtomicTypeToTest.Impulse -> arg(OscAtomics.Impulse)
                }
            }
        }
        // write to bytes
        val actualMessageBytes = writeToByteArray { message.write(it) }
        // compare
        assertEquals("\n${expectedMessageBytes.toHexDumpString()}", "\n${actualMessageBytes.toHexDumpString()}")
    }

}