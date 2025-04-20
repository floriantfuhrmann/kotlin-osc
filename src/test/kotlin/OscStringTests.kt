import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class OscStringTests {

    @Test
    fun testOscStringSizeWithNoPaddingBytes() {
        // example string 1 from https://opensoundcontrol.stanford.edu/spec-1_0-examples.html#osc-message-examples
        val exampleString1 = "/oscillator/4/frequency"
        // this string has 23 characters, so including a null terminator it is 24 bytes long. so a multiple of 4, thus
        // not needing padding.
        // convert to osc string
        val oscString = exampleString1.asOscAtomic
        // check size
        assertEquals(24, oscString.size)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun testOscStringWithPaddingBytes(paddingBytes: Int) {
        // example string 1 from https://opensoundcontrol.stanford.edu/spec-1_0-examples.html#osc-message-examples
        val exampleString1 = "/oscillator/4/frequency"
        // cut the amount of chars of padding we want
        val testString = exampleString1.substring(0, exampleString1.length - paddingBytes)
        // convert to osc string
        val oscString = testString.asOscAtomic
        // check size
        assertEquals(24, oscString.size)
        // check bytes
        val actual = writeToByteArray { oscString.write(it) }
        val expected = ByteArray(24).also {
            // copy test string to expected array
            System.arraycopy(testString.toByteArray(Charsets.US_ASCII), 0, it, 0, testString.length)
        }
        assertEquals("\n"+expected.toHexDumpString(), "\n"+actual.toHexDumpString())
    }

}