package eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle

import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscObject
import java.io.OutputStream

class OscBundleElement(
    val contents: OscObject
) {

    fun write(outputStream: OutputStream) {
        // write element size
        contents.size.asOscAtomic.write(outputStream)
        // write contents
        contents.write(outputStream)
    }

}