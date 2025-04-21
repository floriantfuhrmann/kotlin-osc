package eu.florian_fuhrmann.kotlin_osc.packet.contents.message

import eu.florian_fuhrmann.kotlin_osc.atomics.OscAtomics
import eu.florian_fuhrmann.kotlin_osc.atomics.asOscAtomic
import eu.florian_fuhrmann.kotlin_osc.packet.contents.OscObject
import eu.florian_fuhrmann.kotlin_osc.packet.contents.message.OscMessage.Builder
import java.io.OutputStream
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * An OSC message consists of an address pattern and a list of zero or more
 * arguments.
 *
 * @param addressPattern the osc address pattern of the message
 * @param arguments the osc arguments of the message
 * @see <a href="https://opensoundcontrol.org/spec-1_0">OSC 1.0
 *    Specification</a>
 */
class OscMessage(
    val addressPattern: String, // String could be replaced with an OSC Address Pattern type for validation
    val arguments: List<OscAtomics.AbstractOscAtomic<*>>
) : OscObject {

    constructor(
        addressPattern: String,
        vararg arguments: OscAtomics.AbstractOscAtomic<*>
    ) : this(addressPattern, arguments.toList())

    /**
     * Returns the type tag string representing argument types in this message.
     * The string starts with a comma followed by type tags for each argument.
     * Examples:
     * - ",iff" for one int32 argument followed by two float32 arguments
     * - "," for no arguments
     */
    val typeTagString: String
        get() {
            // initialize string builder with comma
            val stringBuilder = StringBuilder(",")
            // append type tags for each argument
            arguments.map { it.typeTag }.forEach { stringBuilder.append(it) }
            // return string
            return stringBuilder.toString()
        }

    override val size: Int
        get() {
            val addressSize = addressPattern.asOscAtomic.size
            val typeTagSize = typeTagString.asOscAtomic.size
            val size: Int = addressSize + typeTagSize + arguments.sumOf { it.size }
            return size
        }

    override fun write(outputStream: OutputStream) {
        // write the address pattern
        addressPattern.asOscAtomic.write(outputStream)
        // write type tag string
        typeTagString.asOscAtomic.write(outputStream)
        // write arguments
        arguments.forEach { it.write(outputStream) }
    }

    class Builder(private var addressPattern: String = "") {
        private val arguments = mutableListOf<OscAtomics.AbstractOscAtomic<*>>()
        fun addressPattern(addressPattern: String) {
            this.addressPattern = addressPattern
        }

        fun arg(argument: OscAtomics.AbstractOscAtomic<*>) = arguments.add(argument)
        fun arg(argument: Int) = arguments.add(argument.asOscAtomic)
        fun arg(argument: Float) = arguments.add(argument.asOscAtomic)
        fun arg(argument: String) = arguments.add(argument.asOscAtomic)

        @OptIn(ExperimentalTime::class)
        fun arg(argument: Instant) = arguments.add(argument.asOscAtomic)
        fun toOscMessage() = OscMessage(addressPattern, arguments)
    }
}

inline fun buildOscMessage(addressPattern: String, builder: Builder.() -> Unit) =
    Builder(addressPattern).apply(builder).toOscMessage()