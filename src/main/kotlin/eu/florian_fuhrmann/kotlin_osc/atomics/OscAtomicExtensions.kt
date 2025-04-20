package eu.florian_fuhrmann.kotlin_osc.atomics

val Int.asOscAtomic: OscAtomics.Int32
    get() = OscAtomics.Int32(this)

val Float.asOscAtomic: OscAtomics.Float32
    get() = OscAtomics.Float32(this)

val String.asOscAtomic: OscAtomics.OscString
    get() = OscAtomics.OscString(this)
