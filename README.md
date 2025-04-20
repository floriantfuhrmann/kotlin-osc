# kotlin-osc

A lightweight Kotlin implementation of the OSC (Open Sound Control) protocol client. This library provides a simple way
to send OSC messages over TCP connections.

## Features

- Client-side OSC implementation
- TCP support (exclusively)

## Project Status

This project is under active development. Currently, TCP support is the primary focus.

## Motivation

kotlin-osc was created to support [MusicTimedTriggers](https://github.com/floriantfuhrmann/MusicTimedTriggers), which 
requires lightweight OSC client functionality. The intentionally limited scope reflects the specific needs of this 
parent project.

## Alternatives

For more comprehensive OSC implementation needs, consider [JavaOSC](https://github.com/hoijui/JavaOSC), which provides a
more feature-complete solution.

## Protocol Reference

This library implements the [OSC 1.0 Specification](https://opensoundcontrol.stanford.edu/spec-1_0.html).