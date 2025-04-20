package eu.florian_fuhrmann.kotlin_osc.utils

import java.io.OutputStream

/**
 * A stream that can be used to wrap messages into frames for the SLIP
 * protocol.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Serial_Line_Internet_Protocol">Serial Line Internet Protocol</a>
 *
 * @param outputStream the underlying output stream
 */
class SlipOutputStream(
    private val outputStream: OutputStream,
    private val useDoubleEnd: Boolean
) : OutputStream() {

    companion object {
        const val END = 0xC0.toByte()
        const val ESC = 0xDB.toByte()
        const val ESC_END = 0xDC.toByte()
        const val ESC_ESC = 0xDD.toByte()
    }

    /**
     * Indicates whether the stream is currently in a SLIP-encoded frame.
     * Writing is only allowed when this is set to `true`.
     */
    var inFrame = false
        private set

    /** Begin a new SLIP-encoded frame. */
    fun beginFrame() {
        // ensure we're not in a frame
        check(!inFrame) { "Cannot begin a frame while already in a frame" }
        // write END indicating start of frame (for double-ended encoding)
        if(useDoubleEnd) {
            outputStream.write(END.toInt())
        }
        // set in frame flag
        inFrame = true
    }

    /** End the current SLIP-encoded frame. */
    fun endFrame() {
        // ensure we're in a frame
        check(inFrame) { "Cannot end a frame without beginning one" }
        // write END indicating end of frame
        outputStream.write(END.toInt())
        // unset in frame flag
        inFrame = false
    }

    override fun write(b: Int) {
        // ensure we're in a frame
        check(inFrame) { "Cannot write data outside of a frame" }
        // write to underlying stream (escaping END and ESC as needed)
        when (b.toByte()) {
            END -> {
                outputStream.write(ESC.toInt())
                outputStream.write(ESC_END.toInt())
            }

            ESC -> {
                outputStream.write(ESC.toInt())
                outputStream.write(ESC_ESC.toInt())
            }

            else -> {
                outputStream.write(b)
            }
        }
    }

    override fun close() {
        // ensure we're not in a frame
        check(!inFrame) { "Cannot close a frame before ending it" }
        super.close()
    }

}