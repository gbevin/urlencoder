/*
 * Copyright 2001-2022 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.urlencoder;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UrlEncoderTest {
    private String[] invalid = {"sdkjfh%", "sdkjfh%6", "sdkjfh%xx", "sdfjfh%-1"};
    private String same = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~";
    private Map<String, String> validMap = Map.of(
            "a test &", "a%20test%20%26",
            "!abcdefghijklmnopqrstuvwxyz%%ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~=",
            "%21abcdefghijklmnopqrstuvwxyz%25%25ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~%3D",
            "%#okékÉȢ smile!😁", "%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"
    );

    @Test
    public void testDecodeURL() {
        assertNull(UrlEncoder.decode(null));
        assertSame(same, UrlEncoder.decode(same));
        validMap.forEach((expected, source) -> assertEquals(expected, UrlEncoder.decode(source)));

        for (String i : invalid) {
            assertThrows(IllegalArgumentException.class, () -> UrlEncoder.decode(i));
        }
    }

    @Test
    public void testEncodeURL() {
        assertNull(UrlEncoder.encode(null));
        assertTrue(UrlEncoder.encode("").isEmpty());
        assertSame(same, UrlEncoder.encode(same));
        assertSame(same, UrlEncoder.encode(same, ""));
        validMap.forEach((source, expected) -> assertEquals(expected, UrlEncoder.encode(source)));

        assertEquals("?test=a%20test", UrlEncoder.encode("?test=a test", "?="));
        assertEquals("?test=a%20test", UrlEncoder.encode("?test=a test", '?', '='));
        assertEquals("aaa", UrlEncoder.encode("aaa", 'a'));
    }
}
