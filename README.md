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
- Independence from JVM

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

Note: When using [`buildOscMessage {}`](#creating-an-oscmessage-object-using-buildoscmessage--recommended), you don't need to explicitly create instances of most of these types.

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

## OSC Message
This is the Structure of an `OscMessage`:
```kotlin
class OscMessage(
    val addressPattern: String, // the address pattern of this message
    val arguments: List<OscAtomics.AbstractOscAtomic<*>> // zero or more arguments
) : OscObject
```
There currently is no explicit address pattern type, as an address pattern is essentially just a String starting with a
`/` character. Refer to the OSC Specification for details.

### Creating an OscMessage Object Manually (Not Recommended)
You can manually put together an `OscMessage` like this:
```kotlin
val message = OscMessage("/foo/bar", listOf(0.2f.asOscAtomic, 42.asOscAtomic))
// or
val message = OscMessage("/foo/bar", 0.2f.asOscAtomic, 42.asOscAtomic)
```
However, it is strongly recommended to use `buildOscMessage { /* ... */ }` instead.

### Creating an OscMessage Object Using `buildOscMessage {}` (Recommended)
The same message can be created using the builder pattern:
```kotlin
val message = buildOscMessage {
    addressPattern("/foo/bar") // address pattern
    arg(0.2f) // 1st argument
    arg(42)  // 2nd argument
}
// or simply:
val message = buildOscMessage("/test/foo") { arg(0.2f); arg(42) }
```
#### Supported Argument Types
The following types can be passed to `arg(...)`:
```kotlin
val myAbstractAtomic: OscAtomics.AbstractOscAtomic<*> = /* ... */
val message = buildOscMessage("/foo/bar") {
    arg(myAbstractAtomic)                           // AbstractOscAtomic
    arg(42)                                         // Int (-> int32)
    arg(42.0f)                                      // Float (-> float32)
    arg("hello")                                    // String (-> OSC-string)
    arg(Instant.fromEpochMilliseconds(123456789L))  // Instant (-> OSC-timetag)
    arg(OscAtomics.OscTimeTag.immediately())        // Immediate time tag
    arg(byteArrayOf(0x00, 0x01, 0x02))              // ByteArray (-> OSC-blob)
    arg(true)                                       // Boolean (-> True / False)
    arg(null)                                       // null (same as OscAtomics.Null)
    arg(OscAtomics.Impulse)                         // Impulse
}
```

## OSC Bundle
This is the Structure of an `OscBundle`:
```kotlin
class OscBundle(
    val timeTag: OscAtomics.OscTimeTag,     // time-tag (meaning not specified by OSC 1.1 - ignored by most servers)
    val elements: List<OscBundleElement>    // zero or more bundle elements
) : OscObject
```

### Creating an OscBundle Object Manually (Not Recommended)
Similar to `OscMessage` it is possible to manually put together an `OscBundle` like this:
```kotlin
val msg1 = OscMessage("/foo/bar", listOf(42.asOscAtomic))
val bundle = OscBundle(
    timeTag = Instant.fromEpochMilliseconds(123456789L).asOscAtomic,
    elements = listOf(OscBundleElement(msg1))
)
```
However, it is strongly recommended to use `buildOscBundle { /* ... */ }` instead.

### Creating an OscBundle Object Using `buildOscBundle {}` (Recommended)
The same bundle can be created more elegantly using the builder pattern:
```kotlin
val bundle = buildOscBundle {
    timeTag(Instant.fromEpochMilliseconds(987654321L)) // sets this bundles time tag (optional, immediately by default)
    message("/foo") {
        arg(42)
    }
}
```

#### Complex Bundle Structures
The builder pattern makes it easy to create complex bundles with nested structures:
```kotlin
val bundle = buildOscBundle {
    timeTag(Instant.fromEpochMilliseconds(123456789L))
    bundle { // 1st nesting
        message("/foo") { arg(42) }
        message("/bar") { arg(0.3f) }
        bundle { // 2nd nesting
            message("/baz") { arg("hello") }    // these messages
            message("/baz") { arg(42) }         // are encapsulated
            message("/baz") { arg(0.3f) }       // in 3 Bundles
        }
    }
}
```

## OSC Packet
An `OscPacket` is the top-level container encapsulating either an `OscMessage` or `OscBundle`:
```kotlin
class OscPacket(val contents: OscObject) // OscObject is either an OscMessage or an OscBundle
```

### Creating an OscPacket Object
You can create an `OscObject` in two ways:
```kotlin
// Method 1: Direct constructor
val myOscObject: OscObject = /* ... */
val packet = OscPacket(myOscObject)

// Method 2: OscObject#toOscPacket() function
val packet = myOscObject.toOscPacket()
```

### Writing an OscPacket into an OutputStream
Use `OscPacket#write()` to write the packet to any `OutputStream`.
```kotlin
val myOutputStream = /* ... */
packet.write(
    outputStream = myOutputStream,
    oscVersion = OscVersion.Specification1_1 // Optional, default: Specification1_1
)
```
The `oscVersion` parameter determines the framing mechanism:
* `OscVersion.Specification1_0`: Uses size-count-preamble
* `OscVersion.Specification1_1`: double-ended SLIP framing (default)

## OSC Client
The `OscClient` provides a convenient way to send OSC packets over TCP:
```kotlin
class OscClient(
    val socket: Socket,
    val oscVersion: OscVersion = OscVersion.Specification1_1
)
```

### Creating an OscClient
```kotlin
// Create a client with the default OSC 1.1 specification
val client = OscClient(Socket("127.0.0.1", 5000))

// Or specify a different OSC version
val clientWithVersion = OscClient(
    socket = Socket("127.0.0.1", 5000),
    oscVersion = OscVersion.Specification1_0
)
```

### Sending OSC Packets
The client offers multiple ways to send OSC Packets/Messages/Bundles:
```kotlin
// Method 1: Send a pre-built OSC packet
val packet = buildOscMessage("/foo/bar") { arg(42) }.toOscPacket()
client.sendPacket(packet)

// Method 2: Use OscPacket#sendTo function
packet.sendTo(client)

// Method 3: Send messages/bundles/ general objects directly (the client handles packet creation)
val message = buildOscMessage("/foo/bar") { arg("hello") }
client.sendMessage(message)
// or
client.sendBundle(bundle)
// or
client.sendObject(oscObject)
```

### Client Lifecycle Management
Remember to close the socket when done with the client:
```kotlin
try {
    val client = OscClient(Socket("127.0.0.1", 5000))
    client.sendMessage(buildOscMessage("/foo") { arg("hello") })
    // Additional operations...
} finally {
    client.socket.close()
}
```
### Example: Complete Message Sending Flow
```kotlin
// Create and configure the client
val client = OscClient(Socket("127.0.0.1", 5000))

// Build and send an OSC message in a single operation
buildOscMessage {
    addressPattern("/foo/bar")
    arg(42.0f)
}.toOscPacket().sendTo(client)

// Close the socket when done
client.socket.close()
```