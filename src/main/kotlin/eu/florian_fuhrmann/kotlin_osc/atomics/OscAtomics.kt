package eu.florian_fuhrmann.kotlin_osc.atomics

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
        override val size = 4
        override fun write(outputStream: OutputStream) {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array())
        }
    }

    class Float32(value: Float) : AbstractOscAtomic<Float>(value, 'f') {
        override val size = 4
        override fun write(outputStream: OutputStream) {
            outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array())
        }
    }

    class OscString(value: String) : AbstractOscAtomic<String>(value, 's') {
        /**
         * The size of an osc string is the length of the string plus one for the
         * null terminator plus the number of padding bytes needed to make the size
         * a multiple of 4.
         *
         * The number of padding bytes is given by 4 - (size % 4) & 3 where size is the
         * length of the string plus one for the null terminator.
         */
        override val size = (value.length + 1).let { it + ((4 - it % 4) and 0b11) }
        override fun write(outputStream: OutputStream) {
            // write string
            outputStream.write(value.toByteArray(Charsets.US_ASCII))
            // write null terminator and padding bytes
            val paddingBytes = (4 - (value.length + 1) % 4) and 0b11
            outputStream.write(ByteArray(1 + paddingBytes))
        }
    }

}