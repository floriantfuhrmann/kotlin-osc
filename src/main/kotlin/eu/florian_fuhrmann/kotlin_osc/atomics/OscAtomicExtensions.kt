package eu.florian_fuhrmann.kotlin_osc.atomics

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val Int.asOscAtomic: OscAtomics.Int32
    get() = OscAtomics.Int32(this)

val Float.asOscAtomic: OscAtomics.Float32
    get() = OscAtomics.Float32(this)

val String.asOscAtomic: OscAtomics.OscString
    get() = OscAtomics.OscString(this)

@OptIn(ExperimentalTime::class)
val Instant.asOscAtomic: OscAtomics.OscTimeTag
    get() = OscAtomics.OscTimeTag.at(this)

val ByteArray.asOscAtomic: OscAtomics.OscBlob
    get() = OscAtomics.OscBlob(this)