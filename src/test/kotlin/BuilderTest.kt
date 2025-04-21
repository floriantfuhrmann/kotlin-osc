import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.OscPacket
import eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle.OscBundle
import eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle.OscBundleElement
import eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle.buildOscBundle
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.buildOscMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * These Tests just test if the result from the builder is the same as manually creating the same object.
 */
@OptIn(ExperimentalTime::class)
class BuilderTest {

    @Test
    fun testSimpleMessage() {
        // build using builder
        val actual = buildOscMessage {
            addressPattern("/test/foo")
            arg("hello")
            arg(42)
            arg(42.0f)
            arg(Instant.fromEpochMilliseconds(123456789L))
        }.toOscPacket()
        // build manually
        val message = OscMessage("/test/foo", listOf(
            "hello".asOscAtomic,
            42.asOscAtomic,
            42.0f.asOscAtomic,
            Instant.fromEpochMilliseconds(123456789L).asOscAtomic
        ))
        val expected = OscPacket(message)
        // write both as bytes
        val actualBytes = writeToByteArray { actual.write(it) }
        val expectedBytes = writeToByteArray { expected.write(it) }
        // compare bytes
        assertEquals("\n${expectedBytes.toHexDumpString()}", "\n${actualBytes.toHexDumpString()}")
    }

    @Test
    fun testSimpleBundle() {
        // build using builder
        val actual = buildOscBundle {
            timeTag(OscAtomics.OscTimeTag.immediately())
            message {
                addressPattern("/test/foo")
                arg("hello")
                arg(42)
                arg(42.0f)
                arg(Instant.fromEpochMilliseconds(123456789L))
            }
            bundle(timeTag = Instant.fromEpochMilliseconds(987654321L).asOscAtomic) {
                message("/test/bar/1")
                message("/test/bar/2")
            }
        }.toOscPacket()
        // build manually
        val message1 = OscMessage("/test/foo", listOf(
            "hello".asOscAtomic,
            42.asOscAtomic,
            42.0f.asOscAtomic,
            Instant.fromEpochMilliseconds(123456789L).asOscAtomic
        ))
        val message2 = OscMessage("/test/bar/1")
        val message3 = OscMessage("/test/bar/2")
        val bundle = OscBundle(
            timeTag = OscAtomics.OscTimeTag.immediately(),
            elements = listOf(
                OscBundleElement(message1),
                OscBundleElement(OscBundle(
                    timeTag = Instant.fromEpochMilliseconds(987654321L).asOscAtomic,
                    elements = listOf(OscBundleElement(message2), OscBundleElement(message3))
                ))
            )
        )
        val expected = OscPacket(bundle)
        // write both as bytes
        val actualBytes = writeToByteArray { actual.write(it) }
        val expectedBytes = writeToByteArray { expected.write(it) }
        // compare bytes
        assertEquals("\n${expectedBytes.toHexDumpString()}", "\n${actualBytes.toHexDumpString()}")
    }

}