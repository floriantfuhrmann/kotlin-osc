package eu.florian_fuhrmann.kotlin_osc.packet.contents.bundle

import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscObject
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.buildOscMessage
import java.io.OutputStream
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

    class Builder {
        private var timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately()
        private val elements = mutableListOf<OscBundleElement>()

        /** Sets the time-tag of this bundle. Defaults to immediately. */
        fun timeTag(timeTag: OscAtomics.OscTimeTag) {
            this.timeTag = timeTag
        }

        @OptIn(ExperimentalTime::class)
        fun timeTag(instant: Instant) = timeTag(instant.asOscAtomic)

        /** Adds the give [OscBundleElement] to this bundle. */
        fun element(element: OscBundleElement) = elements.add(element)

        /**
         * Wraps the given [OscObject] in an [OscBundleElement] and adds it to this
         * bundle.
         */
        fun element(element: OscObject) = elements.add(OscBundleElement(element))

        /** Adds an osc message to this bundle build by [OscMessage.Builder]. */
        fun message(addressPattern: String = "", builder: OscMessage.Builder.() -> Unit = {}) {
            buildOscMessage(addressPattern, builder).also { element(it) }
        }

        /** Adds an osc bundle to this bundle build by [OscBundle.Builder]. */
        fun bundle(
            timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately(),
            builder: OscBundle.Builder.() -> Unit = {}
        ) {
            buildOscBundle(timeTag, builder).also { element(it) }
        }

        /** Builds an OscBundle from the current state of this builder. */
        fun toOscBundle() = OscBundle(timeTag, elements)
    }

    companion object {
        val BUNDLE_HEADER = "#bundle".asOscAtomic
    }

}

inline fun buildOscBundle(
    timeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately(), builder: OscBundle.Builder.() -> Unit = {}
) = OscBundle.Builder().apply { timeTag(timeTag) }.apply(builder).toOscBundle()