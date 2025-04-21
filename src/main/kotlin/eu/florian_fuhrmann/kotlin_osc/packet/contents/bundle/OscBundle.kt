package eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle

import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscObject
import java.io.OutputStream

/**
 * An OSC bundle consists of a time-tag, followed by a list of zero or more
 * osc bundle elements.
 *
 * @param timeTag the time-tag of this bundle
 * @param elements the elements of this bundle
 */
class OscBundle(
    val timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately(),
    val elements: List<OscBundleElement>
) : OscObject {

    constructor(
        timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately(),
        vararg elements: OscBundleElement
    ) : this(timeTag, elements.toList())

    constructor(
        timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately(),
        vararg elements: OscObject
    ) : this(timeTag, elements.toList().map { OscBundleElement(it) })

    /**
     * The size of this osc bundle in bytes.
     *
     * The size of a bundle is the size of the bundle header plus the size of
     * the time-tag plus the size of all elements, including 4 bytes for the
     * size-preamble.
     */
    override val size = BUNDLE_HEADER.size + timeTag.size + elements.sumOf { 4 + it.contents.size }

    /** Write the contents of this bundle to the given output stream. */
    override fun write(outputStream: OutputStream) {
        // write bundle header
        BUNDLE_HEADER.write(outputStream)
        // write bundles' time-tag
        timeTag.write(outputStream)
        // write bundle elements
        elements.forEach {
            it.write(outputStream)
        }
    }

    companion object {
        val BUNDLE_HEADER = "#bundle".asOscAtomic
    }

}
