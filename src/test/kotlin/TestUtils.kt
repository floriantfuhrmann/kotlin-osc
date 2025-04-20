import java.io.ByteArrayOutputStream
import java.io.OutputStream

fun writeToByteArray(
    block: (OutputStream) -> Unit
): ByteArray {
    val tempStream = ByteArrayOutputStream()
    block(tempStream)
    return tempStream.toByteArray()
}

private fun Char.isPrintable(): Boolean = this in ' '..'~'
fun ByteArray.toHexDumpString(): String {
    val data = this
    return buildString {
        for (i in data.indices step 16) {
            // Print offset
            append(String.format("%04X: ", i))

            // Print hex values
            for (j in 0 until 16) {
                if (i + j < data.size) {
                    append(String.format("%02X ", data[i + j]))
                } else {
                    append("   ")
                }
                if (j == 7) append(" ")
            }

            // Print ASCII representation
            append(" |")
            for (j in 0 until 16) {
                if (i + j < data.size) {
                    val c = data[i + j].toInt().toChar()
                    append(if (c.isPrintable()) c else '.')
                }
            }
            append("|\n")
        }
    }
}
