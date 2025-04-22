# kotlin-osc

A lightweight Kotlin implementation of the OSC (Open Sound Control) protocol/content format targeting the JVM.

## Features

- Client-side OSC implementation
- TCP support (exclusively)
- Support for OSC 1.0 and OSC 1.1

## Motivation

kotlin-osc was created to support [MusicTimedTriggers](https://github.com/floriantfuhrmann/MusicTimedTriggers), which
requires lightweight OSC client functionality. The intentionally limited scope reflects the specific needs of this
parent project.

## Future Planes

- UDP support
- independent from JVM

## Alternatives

For more comprehensive OSC implementation needs, consider [JavaOSC](https://github.com/hoijui/JavaOSC), which provides a
more feature-complete solution.

## Protocol Reference

This library implements the [OSC 1.0 Specification](https://opensoundcontrol.stanford.edu/spec-1_0.html) and 
[OSC 1.1 Specification](https://opensoundcontrol.stanford.edu/spec-1_1.html).

## How to use

### Quick Start - Sending a simple osc message
```kotlin
// Create an osc client
val client = OscClient(Socket("127.0.0.1", 5000))

// send osc message "/foo/bar 0.42"
buildOscMessage {
    addressPattern("/foo/bar")
    arg(0.42f)
}.toOscPacket().sendTo(client)

// alternatively write the osc packet directly into a OutputStream using OscPacket#write()
```

## OSC Atomics
All non-optional Atomic Data Types specified by OSC 1.0 and 1.1 are currently supported.

Note: When using `OscMessage.Builder`, you don't need to explicitly create instances of most of these types.

### Integer
```kotlin
val myOscInt: OscAtomics.Int32 = 42.asOscAtomic
// or
val myOscInt: OscAtomics.Int32 = OscAtomics.Int32(42)
```

### Float
```kotlin
val myOscFloat: OscAtomics.Float32 = 42.0f.asOscAtomic
// or
val myOscFloat: OscAtomics.Float32 = OscAtomics.Float32(42.0f)
```

### String
```kotlin
val myOscString: OscAtomics.OscString = "Hello World".asOscAtomic
// or
val myOscString: OscAtomics.OscString = OscAtomics.OscString("Hello World")
```

### Blob
```kotlin
val myOscBlob: OscAtomics.OscBlob = byteArrayOf(0x2a, 0x2b, 0x2c).asOscAtomic
// or
val myOscBlob: OscAtomics.OscBlob = OscAtomics.OscBlob(byteArrayOf(0x2a, 0x2b, 0x2c))
```

### True / False
```kotlin
val myOscBool: OscAtomics.OscBool = true.asOscAtomic
// or
val myOscBool: OscAtomics.OscBool = OscAtomics.True
// Note: `OscAtomics.OscBool(true)` is not possible
```

### Null
```kotlin
val myOscNull: OscAtomics.OscNull = OscAtomics.Null
```

### Impulse
```kotlin
val myOscImpulse: OscAtomics.OscImpulse = OscAtomics.Impulse
```

### Timetag
#### Specified
```kotlin
val instant: Instant = Instant.fromEpochMilliseconds(123456789L)

val myOscTimeTag: OscAtomics.OscTimeTag = instant.asOscAtomic
// or
val myOscTimeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.Specified(instant)
// or
val myOscTimeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.at(instant)
```
#### Immediate
```kotlin
val myOscTimeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.Immediate()
// or
val myOscTimeTag: OscAtomics.OscTimeTag = OscAtomics.OscTimeTag.immediately()
```

## Osc Message
TODO

## Osc Bundle
TODO

## Osc Packet
TODO

## Osc Client
TODO
