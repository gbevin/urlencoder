/*
 * Copyright 2001-2023 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.urlencoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Most defensive approach to URL encoding and decoding.
 * <p>
 * Rules determined by combining the unreserved character set from
 * <a href="https://www.rfc-editor.org/rfc/rfc3986#page-13">RFC 3986</a> with
 * the percent-encode set from
 * <a href="https://url.spec.whatwg.org/#application-x-www-form-urlencoded-percent-encode-set">application/x-www-form-urlencoded</a>.
 * <p>
 * Both specs above support percent decoding of two hexadecimal digits to a
 * binary octet, however their unreserved set of characters differs and
 * {@code application/x-www-form-urlencoded} adds conversion of space to +,
 * which has the potential to be misunderstood.
 * <p>
 * This class encodes with rules that will be decoded correctly in either case.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author Erik C. Thauvin (erik@thauvin.net)
 * @since 1.0
 */
public final class UrlEncoder {
    static final BitSet UNRESERVED_URI_CHARS;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    static {
        // see https://www.rfc-editor.org/rfc/rfc3986#page-13
        // and https://url.spec.whatwg.org/#application-x-www-form-urlencoded-percent-encode-set
        var unreserved = new BitSet('z' + 1);
        unreserved.set('-');
        unreserved.set('.');
        for (int c = '0'; c <= '9'; ++c) unreserved.set(c);
        for (int c = 'A'; c <= 'Z'; ++c) unreserved.set(c);
        unreserved.set('_');
        for (int c = 'a'; c <= 'z'; ++c) unreserved.set(c);
        UNRESERVED_URI_CHARS = unreserved;
    }

    private UrlEncoder() {
        // no-op
    }

    private static void appendUrlEncodedByte(StringBuilder out, int ch) {
        out.append("%");
        appendUrlEncodedDigit(out, ch >> 4);
        appendUrlEncodedDigit(out, ch);
    }

    private static void appendUrlEncodedDigit(StringBuilder out, int digit) {
        out.append(HEX_DIGITS[digit & 0x0F]);
    }

    /**
     * Transforms a provided <code>String</code> URL into a new string,
     * containing decoded URL characters in the UTF-8 encoding.
     *
     * @param source The string URL that has to be decoded
     * @return The decoded <code>String</code> object.
     * @see #encode(String, String)
     * @since 1.0
     */
    public static String decode(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        var length = source.length();
        StringBuilder out = null;
        char ch;
        byte[] bytes_buffer = null;
        var bytes_pos = 0;
        var i = 0;
        while (i < length) {
            ch = source.charAt(i);

            if (ch == '%') {
                if (out == null) {
                    out = new StringBuilder(length);
                    out.append(source, 0, i);
                }

                if (bytes_buffer == null) {
                    // the remaining characters divided by the length
                    // of the encoding format %xx, is the maximum number of
                    // bytes that can be extracted
                    bytes_buffer = new byte[(length - i) / 3];
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
                    throw new IllegalArgumentException("Illegal characters in escape sequence: " + e.getMessage(), e);
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
        return encode(source, null, false);
    }

    /**
     * Transforms a provided <code>String</code> object into a new string,
     * containing only valid URL characters in the UTF-8 encoding.
     *
     * @param source The string that has to be transformed into a valid URL
     *               string.
     * @param allow  Additional characters to allow.
     * @return The encoded <code>String</code> object.
     * @see #decode(String)
     * @since 1.0
     */
    public static String encode(String source, String allow) {
        return encode(source, allow, false);
    }

    /**
     * Transforms a provided <code>String</code> object into a new string,
     * containing only valid URL characters in the UTF-8 encoding.
     *
     * @param source      The string that has to be transformed into a valid URL
     *                    string.
     * @param spaceToPlus Convert any space to {@code +}.
     * @return The encoded <code>String</code> object.
     * @see #decode(String)
     * @since 1.0
     */
    public static String encode(String source, boolean spaceToPlus) {
        return encode(source, null, spaceToPlus);
    }

    /**
     * Transforms a provided <code>String</code> object into a new string,
     * containing only valid URL characters in the UTF-8 encoding.
     *
     * @param source      The string that has to be transformed into a valid URL
     *                    string.
     * @param allow       Additional characters to allow.
     * @param spaceToPlus Convert any space to {@code +}.
     * @return The encoded <code>String</code> object.
     * @see #decode(String)
     * @since 1.0
     */
    public static String encode(String source, String allow, boolean spaceToPlus) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        StringBuilder out = null;
        char ch;
        var i = 0;
        while (i < source.length()) {
            ch = source.charAt(i);
            if (isUnreservedUriChar(ch) || (allow != null && allow.indexOf(ch) != -1)) {
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
                    if (spaceToPlus && ch == ' ') {
                        out.append('+');
                    } else {
                        appendUrlEncodedByte(out, cp);
                    }
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

    // see https://www.rfc-editor.org/rfc/rfc3986#page-13
    // and https://url.spec.whatwg.org/#application-x-www-form-urlencoded-percent-encode-set
    private static boolean isUnreservedUriChar(char ch) {
        return ch <= 'z' && UNRESERVED_URI_CHARS.get(ch);
    }

    /**
     * Main method to encode/decode URLs on the command line
     *
     * @param arguments the command line arguments
     * @since 1.1
     */
    public static void main(String[] arguments) {
        try {
            var result = processMain(arguments);
            if (result.status == 0) {
                System.out.println(result.output);
            } else {
                System.err.println(result.output);
            }
            System.exit(result.status);
        } catch (IllegalArgumentException e) {
            System.err.println(UrlEncoder.class.getSimpleName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    static MainResult processMain(String... arguments) {
        var valid_arguments = false;
        var perform_decode = false;
        var args = new ArrayList<>(List.of(arguments));
        if (!args.isEmpty() && args.get(0).startsWith("-")) {
            var option = args.remove(0);
            if (("-d").equals(option)) {
                perform_decode = true;
                valid_arguments = (args.size() == 1);
            } else if (("-e").equals(option)) {
                valid_arguments = (args.size() == 1);
            } else {
                args.clear();
            }
        }

        var text = "";
        if (args.size() == 1 && !args.get(0).isEmpty()) {
            text = args.remove(0);
            valid_arguments = true;
        }

        if (!valid_arguments) {
            return new MainResult("Usage : java -jar urlencoder-*.jar [-ed] text" + System.lineSeparator() +
                                  "Encode and decode URL components defensively." + System.lineSeparator() +
                                  "  -e  encode (default)" + System.lineSeparator() +
                                  "  -d  decode", 1);
        }
        if (perform_decode) {
            return new MainResult(UrlEncoder.decode(text), 0);
        } else {
            return new MainResult(UrlEncoder.encode(text), 0);
        }
    }

    static class MainResult {
        final String output;
        final int status;

        public MainResult(String output, int status) {
            this.output = output;
            this.status = status;
        }
    }
}
