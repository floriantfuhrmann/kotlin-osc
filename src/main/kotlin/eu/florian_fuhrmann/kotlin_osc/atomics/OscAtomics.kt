package eu.florian_fuhrmann.kotlin_osc.atomics

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class OscAtomics {

    /** A atomic data element like an int32, float32, OscString, etc. */
    sealed class AbstractOscAtomic<T>(
        val value: T, val typeTag: Char
    ) {
        /** The size of this osc data atomic in bytes. */
        abstract val size: Int

        /** Writes this atomics value data to the given output stream. */
        abstract fun write(outputStream: OutputStream)
    }

    class Int32(value: Int) : AbstractOscAtomic<Int>(value, 'i') {
        override val size = SIZE
        override fun write(outputStream: OutputStream) {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array())
        }

        companion object {
            const val SIZE = 4
        }
    }

    @OptIn(ExperimentalTime::class)
    sealed class OscTimeTag(value: Instant?): AbstractOscAtomic<Instant?>(value, 't') {
        override val size = SIZE

        class Immediate : OscTimeTag(null) {
            override fun write(outputStream: OutputStream) {
                // write 7 times 0x00 and then 0x01
                repeat(7) { outputStream.write(0) }
                outputStream.write(1)
            }
        }

        class Specified(timeValue: Instant) : OscTimeTag(timeValue) {
            private fun writeLowerBytesOfLong(long: Long, outputStream: OutputStream) {
                outputStream.write((long shr 24).toByte().toInt())
                outputStream.write((long shr 16).toByte().toInt())
                outputStream.write((long shr 8).toByte().toInt())
                outputStream.write((long shr 0).toByte().toInt())
            }
            override fun write(outputStream: OutputStream) {
                check(value != null) { "Cannot write a specified time tag with a null value" }
                // write time in NTP format (see https://en.wikipedia.org/wiki/Network_Time_Protocol#Timestamps)
                // write the number of seconds since 1900
                val secondsSince1900 = value.epochSeconds + 2208988800L
                writeLowerBytesOfLong(secondsSince1900, outputStream)
                // write the fractional part
                val fraction = (value.nanosecondsOfSecond * 0x100000000L) / 1_000_000_000L
                writeLowerBytesOfLong(fraction, outputStream)
            }
        }

        companion object {
            const val SIZE = 8
            fun immediately(): OscTimeTag = Immediate()
            fun at(instant: Instant): OscTimeTag = Specified(instant)
        }
    }

    class Float32(value: Float) : AbstractOscAtomic<Float>(value, 'f') {
        override val size = SIZE
        override fun write(outputStream: OutputStream) {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array())
        }

        companion object {
            const val SIZE = 4
        }
    }

    class OscString(value: String) : AbstractOscAtomic<String>(value, 's') {
        /**
         * The size of an osc string is the length of the string plus one for the
         * null terminator plus the number of padding bytes needed to make the size
         * a multiple of 4.
         *
         * The number of padding bytes including the null terminator is given by
         * 4 - (size % 4) where size is the length of the string without the null
         * terminator.
         */
        override val size = value.length.let { it + (4 - it % 4) }
        override fun write(outputStream: OutputStream) {
            // write string
            outputStream.write(value.toByteArray(Charsets.US_ASCII))
            // write null terminator and padding bytes
            val paddingBytes = (4 - value.length % 4)
            outputStream.write(ByteArray(paddingBytes))
        }
    }

    class OscBlob(value: ByteArray) : AbstractOscAtomic<ByteArray>(value, 'b') {
        /**
         * The size of an osc blob is the size of the blob data plus 4 for the
         * int32 size header plus the number of padding bytes needed to make the
         * size a multiple of 4.
         *
         * The number of padding bytes is given by 4 - (size % 4) where size is the
         * length of the blob data.
         */
        override val size = Int32.SIZE + value.size.let { it + ((4 - it % 4) and 0b11) }

        /**
         * Writes the blob data to the given output stream following the OSC format
         * specification:
         * 1. First writes an int32 indicating the size of blob data
         * 2. Then writes the actual blob data bytes
         * 3. Finally, writes 0-3 null bytes as padding to ensure the total length
         *    is a multiple of 4 bytes
         *
         * For example, a blob of size 6 would be written as:
         * - Size value (4 bytes): 0x00 0x00 0x00 0x06
         * - Blob data (6 bytes): 0xXX 0xXX 0xXX 0xXX 0xXX 0xXX
         * - Padding (2 bytes): 0x00 0x00
         *
         * @param outputStream The output stream to write the blob data to
         */
        override fun write(outputStream: OutputStream) {
            // write the size count preamble
            value.size.asOscAtomic.write(outputStream)
            // write the blob
            outputStream.write(value)
            // write padding bytes
            val paddingBytes = (4 - value.size % 4) and 0b11
            outputStream.write(ByteArray(paddingBytes))
        }
    }

    sealed class OscBool(typeTag: Char) : AbstractOscAtomic<Unit>(Unit, typeTag) {
        override val size = SIZE

        /**
         * OscTrue and OscFalse have no data, so this method writes nothing to the
         * given output stream.
         */
        override fun write(outputStream: OutputStream) {
            // true has no data, so there is nothing to write
        }

        companion object {
            const val SIZE = 0
        }
    }

    class OscTrue : OscBool('T')
    class OscFalse : OscBool('F')

}