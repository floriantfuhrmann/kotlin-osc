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
```kt
// Create an osc client
val client = OscClient(Socket("127.0.0.1", 5000))

// send osc message "/foo/bar 0.42"
buildOscMessage {
    addressPattern("/foo/bar")
    arg(0.42f)
}.toOscPacket().sendTo(client)

// alternatively write the osc packet directly into a OutputStream using OscPacket#write()
```

## Osc Atomics
TODO

## Osc Message
TODO

## Osc Bundle
TODO

## Osc Client
TODO
