/*
 * Copyright 2001-2022 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.urlencoder;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * URL encoding and decoding.
 * <p>
 * Rules determined by <a href="https://www.rfc-editor.org/rfc/rfc3986#page-13">RFC 3986</a>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */
public class UrlEncoder {
    /**
     * Transforms a provided <code>String</code> object into a new string,
     * containing only valid URL characters in the UTF-8 encoding.
     *
     * @param source The string that has to be transformed into a valid URL
     *               string.
     * @return The encoded <code>String</code> object.
     * @see #decode(String)
     * @since 1.0
     */
    public static String encode(String source) {
        if (source == null) {
            return null;
        }

        StringBuilder out = null;
        char ch;
        for (var i = 0; i < source.length(); ) {
            ch = source.charAt(i);
            if (isUnreservedUriChar(ch)) {
                if (out != null) {
                    out.append(ch);
                }
                i += 1;
            } else {
                if (out == null) {
                    out = new StringBuilder(source.length());
                    out.append(source, 0, i);
                }

                var cp = source.codePointAt(i);
                if (cp < 0x80) {
                    appendUrlEncodedByte(out, cp);
                    i += 1;
                } else if (Character.isBmpCodePoint(cp)) {
                    for (var b : Character.toString(ch).getBytes(StandardCharsets.UTF_8)) {
                        appendUrlEncodedByte(out, b);
                    }
                    i += 1;
                } else if (Character.isSupplementaryCodePoint(cp)) {
                    var high = Character.highSurrogate(cp);
                    var low = Character.lowSurrogate(cp);
                    for (var b : new String(new char[]{high, low}).getBytes(StandardCharsets.UTF_8)) {
                        appendUrlEncodedByte(out, b);
                    }
                    i += 2;
                }
            }
        }

        if (out == null) {
            return source;
        }

        return out.toString();
    }

    static final BitSet UNRESERVED_URI_CHARS;

    static {
        // see https://www.rfc-editor.org/rfc/rfc3986#page-13
        var unreserved = new BitSet('~' + 1);
        unreserved.set('-');
        unreserved.set('.');
        for (int c = '0'; c <= '9'; ++c) unreserved.set(c);
        for (int c = 'A'; c <= 'Z'; ++c) unreserved.set(c);
        unreserved.set('_');
        for (int c = 'a'; c <= 'z'; ++c) unreserved.set(c);
        unreserved.set('~');
        UNRESERVED_URI_CHARS = unreserved;
    }

    // see https://www.rfc-editor.org/rfc/rfc3986#page-13
    private static boolean isUnreservedUriChar(char ch) {
        if (ch > '~') return false;
        return UNRESERVED_URI_CHARS.get(ch);
    }

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    private static void appendUrlEncodedDigit(StringBuilder out, int digit) {
        out.append(HEX_DIGITS[digit & 0x0F]);
    }

    private static void appendUrlEncodedByte(StringBuilder out, int ch) {
        out.append("%");
        appendUrlEncodedDigit(out, ch >> 4);
        appendUrlEncodedDigit(out, ch);
    }

    /**
     * Transforms a provided <code>String</code> URL into a new string,
     * containing decoded URL characters in the UTF-8 encoding.
     *
     * @param source The string URL that has to be decoded
     * @return The decoded <code>String</code> object.
     * @see #encode(String)
     * @since 1.0
     */
    public static String decode(String source) {
        if (source == null) {
            return source;
        }

        var length = source.length();
        StringBuilder out = null;
        char ch;
        byte[] bytes_buffer = null;
        var bytes_pos = 0;
        for (var i = 0; i < length; ) {
            ch = source.charAt(i);

            if (ch == '%') {
                if (out == null) {
                    out = new StringBuilder(source.length());
                    out.append(source, 0, i);
                }

                if (bytes_buffer == null) {
                    // the remaining characters divided by the length
                    // of the encoding format %xx, is the maximum number of
                    // bytes that can be extracted
                    bytes_buffer = new byte[(length - i) / 3];
                    bytes_pos = 0;
                }

                i += 1;
                if (length < i + 2) {
                    throw new IllegalArgumentException("Illegal escape sequence");
                }
                try {
                    var v = Integer.parseInt(source, i, i + 2, 16);
                    if (v < 0 || v > 0xFF) {
                        throw new IllegalArgumentException("Illegal escape value");
                    }

                    bytes_buffer[bytes_pos++] = (byte) v;

                    i += 2;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Illegal characters in escape sequence" + e.getMessage());
                }
            } else {
                if (bytes_buffer != null) {
                    out.append(new String(bytes_buffer, 0, bytes_pos, StandardCharsets.UTF_8));

                    bytes_buffer = null;
                    bytes_pos = 0;
                }

                if (out != null) {
                    out.append(ch);
                }

                i += 1;
            }
        }

        if (out == null) {
            return source;
        }

        if (bytes_buffer != null) {
            out.append(new String(bytes_buffer, 0, bytes_pos, StandardCharsets.UTF_8));
        }

        return out.toString();
    }
}
