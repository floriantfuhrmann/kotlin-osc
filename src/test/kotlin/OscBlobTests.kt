import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class OscBlobTests {

    class BlobTestData : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            return Stream.of(
                Arguments.of(
                    byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08) // expected
                ),
                Arguments.of(
                    byteArrayOf(), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x00) // expected
                ),
                Arguments.of(
                    byteArrayOf(0x2a), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x2a, 0x00, 0x00, 0x00) // expected
                ),
                Arguments.of(
                    byteArrayOf(0x2a, 0x2b), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x02, 0x2a, 0x2b, 0x00, 0x00) // expected
                ),
                Arguments.of(
                    byteArrayOf(0x2a, 0x2b, 0x2c), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x03, 0x2a, 0x2b, 0x2c, 0x00) // expected
                ),
                Arguments.of(
                    byteArrayOf(0x2a, 0x2b, 0x2c, 0x2d), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x04, 0x2a, 0x2b, 0x2c, 0x2d) // expected
                ),
                Arguments.of(
                    byteArrayOf(0x2a, 0x2b, 0x2c, 0x2d, 0x2e), // input
                    byteArrayOf(0x00, 0x00, 0x00, 0x05, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x00, 0x00, 0x00) // expected
                ),
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BlobTestData::class)
    fun testOscStringSizeWithNoPaddingBytes(input: ByteArray, expected: ByteArray) {
        // create osc blob from input
        val blob = input.asOscAtomic
        // write to bytes
        val actual = writeToByteArray { blob.write(it) }
        // compare
        assertEquals("\n${expected.toHexDumpString()}", "\n${actual.toHexDumpString()}")
    }

}