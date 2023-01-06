/*
 * Copyright 2001-2023 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.urlencoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UrlEncoderTest {
    private final String same = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.";

    private static Stream<String> invalid() {
        return Stream.of("sdkjfh%", "sdkjfh%6", "sdkjfh%xx", "sdfjfh%-1");
    }

    private static Stream<Arguments> validMap() {
        return Stream.of(
                arguments("a test &", "a%20test%20%26"),
                arguments(
                        "!abcdefghijklmnopqrstuvwxyz%%ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~=",
                        "%21abcdefghijklmnopqrstuvwxyz%25%25ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.%7E%3D"
                ),
                arguments("%#okékÉȢ smile!😁", "%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"),
                arguments(
                        "\uD808\uDC00\uD809\uDD00\uD808\uDF00\uD808\uDD00",
                        "%F0%92%80%80%F0%92%94%80%F0%92%8C%80%F0%92%84%80"
                )
        );
    }

    @Test
    void testDecodeNotNeeded() {
        assertSame(same, UrlEncoder.decode(same));
        assertEquals("", UrlEncoder.decode(""), "decode('')");
        assertEquals(" ", UrlEncoder.decode(" "), "decode(' ')");
    }

    @ParameterizedTest(name = "decode({0}) should be {1}")
    @MethodSource("validMap")
    void testDecodeUrl(String expected, String source) {
        assertEquals(expected, UrlEncoder.decode(source));
    }

    @ParameterizedTest(name = "decode({0})")
    @MethodSource("invalid")
    void testDecodeWithException(String source) {
        assertThrows(IllegalArgumentException.class, () -> UrlEncoder.decode(source), "decode(" + source + ")");
    }

    @Test
    void testDecodeWithNull() {
        assertNull(UrlEncoder.decode(null), "decode(null)");
    }

    @ParameterizedTest(name = "encode({0}) should be {1}")
    @MethodSource("validMap")
    void testEncodeUrl(String source, String expected) {
        assertEquals(expected, UrlEncoder.encode(source));
    }

    @Test
    void testEncodeWhenNoneNeeded() {
        assertSame(same, UrlEncoder.encode(same));
        assertSame(same, UrlEncoder.encode(same, ""), "with empty allow");
    }

    @Test
    void testEncodeWithAllowArg() {
        assertEquals("?test=a%20test", UrlEncoder.encode("?test=a test", "=?"), "encode(x, =?)");
        assertEquals("aaa", UrlEncoder.encode("aaa", "a"), "encode(aaa, a)");
        assertEquals(" ", UrlEncoder.encode(" ", " "), "encode(' ', ' ')");
    }

    @Test
    void testEncodeWithEmptyOrBlank() {
        assertTrue(UrlEncoder.encode("", "").isEmpty(), "encode('','')");
        assertEquals("", UrlEncoder.encode(""), "encode('')");
        assertEquals("%20", UrlEncoder.encode(" "), "encode('')");
    }

    @Test
    void testEncodeWithNulls() {
        assertNull(UrlEncoder.encode(null), "encode(null)");
        assertNull(UrlEncoder.encode(null, null), "encode(null, null)");
        assertEquals("foo", UrlEncoder.encode("foo",  null), "encode(foo, null");
    }

    @Test
    void testEncodeSpaceToPlus() {
        assertEquals("foo+bar", UrlEncoder.encode("foo bar", true));
        assertEquals("foo+bar++foo", UrlEncoder.encode("foo bar  foo", true));
        assertEquals("foo bar", UrlEncoder.encode("foo bar", " ", true));
    }

    @ParameterizedTest(name = "processMain(-d {1}) should be {0}")
    @MethodSource("validMap")
    void testMainDecode(String expected, String source) {
        var result = UrlEncoder.processMain(new String[]{"-d", source});
        assertEquals(expected, result.output);
        assertEquals(0, result.status, "processMain(-d " + source + ").status");
    }

    @ParameterizedTest(name = "processMain(-e {0})")
    @MethodSource("validMap")
    void testMainEncode(String source, String expected) {
        var result = UrlEncoder.processMain(new String[]{source});
        assertEquals(expected, result.output);
        assertEquals(0, result.status, "processMain(-e " + source + ").status");
    }

    @ParameterizedTest(name = "processMain(-d {0})")
    @MethodSource("invalid")
    void testMainEncodeWithExceptions(String source) {
        assertThrows(IllegalArgumentException.class, () -> UrlEncoder.processMain(new String[]{"-d", source}), source);
    }

    @Test
    void testMainTooManyArgs() {
        assertTrue(UrlEncoder.processMain(new String[]{"foo", "bar", "test"}).output.contains("Usage :"), "too many args");
    }

    @Test
    void testMainWithEmptyArgs() {
        assertTrue(UrlEncoder.processMain(new String[]{" ", " "}).output.contains("Usage :"), "processMain(' ', ' ')");
        assertTrue(UrlEncoder.processMain(new String[]{"foo", " "}).output.contains("Usage :"), "processMain('foo', ' ')");
        assertTrue(UrlEncoder.processMain(new String[]{" ", "foo"}).output.contains("Usage :"), "processMain(' ', 'foo')");
        assertTrue(UrlEncoder.processMain(new String[]{"-d ", ""}).output.contains("Usage :"), "processMain('-d', '')");
        assertEquals("%20", UrlEncoder.processMain(new String[]{"-e", " "}).output, "processMain('-e', ' ')");
        assertEquals(" ", UrlEncoder.processMain(new String[]{"-d", " "}).output, "processMain('-d', ' ')");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "-d", "-e"})
    void testMainWithInvalidArgs(String arg) {
        var result = UrlEncoder.processMain(new String[]{arg});
        assertTrue(result.output.contains("Usage :"), "processMain('" + arg + "')");
        assertEquals(1, result.status, "processMain('" + arg + "').status");
    }

    @ParameterizedTest(name = "processMain(-e {0})")
    @MethodSource("validMap")
    void testMainWithOption(String source, String expected) {
        var result = UrlEncoder.processMain(new String[]{"-e", source});
        assertEquals(expected, result.output);
        assertEquals(0, result.status, "processMain(-e " + source + ").status");
    }

    @Test
    void testMainWithUnknownOptions() {
        assertTrue(UrlEncoder.processMain(new String[]{"-p"}).output.contains("Usage :"), "processMain(-p)");
        assertTrue(UrlEncoder.processMain(new String[]{"-"}).output.contains("Usage :"), "processMain(-)");
    }
}