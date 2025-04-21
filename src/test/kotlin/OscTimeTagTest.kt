import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class OscTimeTagTest {

    private fun ByteArray.toBigEndianLong(): Long {
        var result = 0L
        for(i in 0 until size) {
            result = result shl 8
            result = result or (this[i].toLong() and 0xFF)
        }
        return result
    }

    @ParameterizedTest
    @ValueSource(strings = ["2025-01-01T00:00:12.3456789Z", "2025-01-01T00:00:12.00Z", "2025-01-01T00:00:00.00Z", "2026-09-27T19:02:34.567Z", "2028-02-29T14:32:10.123Z"])
    fun testSpecifiedTimeTags(input: String) {
        // get a test input instant
        val inputInstant = Instant.parse(input)
        // create time tag from the input instant
        val oscTimeTag = OscAtomics.OscTimeTag.at(inputInstant)
        // check whether the size is correct
        assertEquals(oscTimeTag.size, 8)

        // write time tag to the byte array
        val bytes = writeToByteArray { oscTimeTag.write(it) }

        // do the backwards conversion
        // get the seconds part (first 4 bytes in big-endian minus 70 years)
        val secondsSince1900 = bytes.copyOfRange(0, 4).toBigEndianLong()
        val secondsSince1970 = secondsSince1900 - 2208988800L
        // get fraction part (last 4 bytes in big-endian)
        val fraction = bytes.copyOfRange(4, 8).toBigEndianLong()
        // convert the fraction to nanoseconds
        val nanoseconds = (fraction * 1_000_000_000L) / 0x100000000
        // convert to the actual instant
        val actual = Instant.fromEpochSeconds(secondsSince1970, nanoseconds)

        // check whether the input and actual are close enough
        assert(inputInstant.minus(actual).absoluteValue <= 1.microseconds) {
            "expected: $inputInstant, actual: $actual"
        }
    }

    @Test
    fun testImmediateTimeTag() {
        // create immediate time tag
        val oscTimeTag = OscAtomics.OscTimeTag.immediately()
        // check whether the size is correct
        assertEquals(oscTimeTag.size, 8)
        // check whether the instant is null
        assert(OscAtomics.OscTimeTag.immediately().value == null) {
            "expected: null, actual: ${OscAtomics.OscTimeTag.immediately().value}"
        }
        // write time tag to the byte array
        val bytes = writeToByteArray { oscTimeTag.write(it) }
        // check whether the bytes are all zero except for the last byte
        val expectedBytes = ByteArray(8) { 0x00 }
        expectedBytes[7] = 0x01
        assert(bytes.contentEquals(expectedBytes)) {
            "expected: ${expectedBytes.toHexDumpString()}, actual: ${bytes.toHexDumpString()}"
        }
    }

}