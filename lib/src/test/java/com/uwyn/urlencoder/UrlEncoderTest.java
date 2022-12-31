/*
 * Copyright 2001-2022 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.urlencoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlEncoderTest {
    @Test
    public void testEncodeURL() {
        assertNull(UrlEncoder.encode(null));
        assertEquals("a%20test%20%26", UrlEncoder.encode("a test &"));
        String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~";
        assertSame(valid, UrlEncoder.encode(valid));
        assertEquals("%21abcdefghijklmnopqrstuvwxyz%25%25ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~%3D", UrlEncoder.encode("!abcdefghijklmnopqrstuvwxyz%%ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~="));
        assertEquals("%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81", UrlEncoder.encode("%#okékÉȢ smile!😁"));
    }

    @Test
    public void testDecodeURL() {
        assertNull(UrlEncoder.decode(null));
        assertEquals("a test &", UrlEncoder.decode("a%20test%20%26"));
        String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~";
        assertSame(valid, UrlEncoder.decode(valid));
        assertEquals("!abcdefghijklmnopqrstuvwxyz%%ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~=", UrlEncoder.decode("%21abcdefghijklmnopqrstuvwxyz%25%25ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789-_.~%3D"));
        assertEquals("%#okékÉȢ smile!😁", UrlEncoder.decode("%25%23ok%C3%A9k%C3%89%C8%A2%20smile%21%F0%9F%98%81"));

        try {
            UrlEncoder.decode("sdkjfh%");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            UrlEncoder.decode("sdkjfh%6");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            UrlEncoder.decode("sdkjfh%xx");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
}
