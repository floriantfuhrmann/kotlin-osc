import eu.florian_fuhrmann.kotlin_osc.utils.SlipOutputStream
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class SlipTest {
    class SingleMessageTestData : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            return Stream.of<Arguments>(
                // Simple message 1 (only one byte) with double end
                Arguments.of(
                    byteArrayOf('A'.code.toByte()),
                    byteArrayOf(0xC0.toByte(), 'A'.code.toByte(), 0xC0.toByte()),
                    true
                ),
                // Simple message 2 (two bytes) with double end
                Arguments.of(
                    byteArrayOf('H'.code.toByte(), 'i'.code.toByte()),
                    byteArrayOf(0xC0.toByte(), 'H'.code.toByte(), 'i'.code.toByte(), 0xC0.toByte()),
                    true
                ),
                // Empty message with double end
                Arguments.of(
                    byteArrayOf(),
                    byteArrayOf(0xC0.toByte(), 0xC0.toByte()),
                    true
                ),
                // Simple message 1 (only one byte) without double end
                Arguments.of(
                    byteArrayOf('A'.code.toByte()),
                    byteArrayOf('A'.code.toByte(), 0xC0.toByte()),
                    false
                ),
                // Simple message 2 (two bytes) without double end
                Arguments.of(
                    byteArrayOf('H'.code.toByte(), 'i'.code.toByte()),
                    byteArrayOf('H'.code.toByte(), 'i'.code.toByte(), 0xC0.toByte()),
                    false
                ),
                // Empty message without double end
                Arguments.of(
                    byteArrayOf(),
                    byteArrayOf(0xC0.toByte()),
                    false
                )
            )
        }
    }

    fun slipEncode(messages: Array<ByteArray>, useDoubleEnd: Boolean): ByteArray {
        return writeToByteArray {
            val slipStream = SlipOutputStream(it, useDoubleEnd = useDoubleEnd)
            messages.forEach { message ->
                slipStream.beginFrame()
                slipStream.write(message)
                slipStream.endFrame()
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SingleMessageTestData::class)
    fun testSingleMessage(input: ByteArray, expected: ByteArray, useDoubleEnd: Boolean) {
        val actual = slipEncode(arrayOf(input), useDoubleEnd = useDoubleEnd)
        assert(expected.contentEquals(actual)) {
            "expected:\n${expected.toHexDumpString()}actual:\n${actual.toHexDumpString()}"
        }
    }

    @Test
    fun testMultipleMessages() {
        val expected = byteArrayOf(
            0xC0.toByte(), 'A'.code.toByte(), 0xC0.toByte(),
            0xC0.toByte(), 'B'.code.toByte(), 0xC0.toByte()
        )
        val actual = slipEncode(
            arrayOf(
                byteArrayOf('A'.code.toByte()),
                byteArrayOf('B'.code.toByte())
            ),
            useDoubleEnd = true
        )
        assert(expected.contentEquals(actual)) {
            "expected:\n${expected.toHexDumpString()}actual:\n${actual.toHexDumpString()}"
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SingleMessageTestData::class)
    fun testSingleMessage3Times(input: ByteArray, expected: ByteArray, useDoubleEnd: Boolean) {
        val expected = ByteArray(expected.size * 3) { expected[it % expected.size] }
        val actual = slipEncode(
            arrayOf(input, input, input),
            useDoubleEnd = useDoubleEnd
        )
        assert(expected.contentEquals(actual)) {
            "expected:\n${expected.toHexDumpString()}actual:\n${actual.toHexDumpString()}"
        }
    }

    @Test
    fun testSpecialCharacters() {
        val input = byteArrayOf(0xC0.toByte(), 0xDB.toByte())
        val expected = byteArrayOf(
            0xC0.toByte(), // begin frame
            0xDB.toByte(), 0xDC.toByte(), // ESC, ESC_END replaces END
            0xDB.toByte(), 0xDD.toByte(), // ESC, ESC_ESC replaces ESC
            0xC0.toByte() // end frame
        )
        val actual = slipEncode(arrayOf(input), useDoubleEnd = true)
        assert(expected.contentEquals(actual)) {
            "expected:\n${expected.toHexDumpString()}actual:\n${actual.toHexDumpString()}"
        }
    }

}