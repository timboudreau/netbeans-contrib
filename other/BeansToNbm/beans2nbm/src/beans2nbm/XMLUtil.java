/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package beans2nbm;

import java.io.CharConversionException;
import java.io.IOException;

 /**
  * Code borrowed from openide/util 
  * 
  * @author  Petr Kuzel */
public final class XMLUtil extends Object {
    private static final char[] DEC2HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /** Forbids creating new XMLUtil */
    private XMLUtil() {
    }


    /**
     * Escape passed string as XML attibute value
     * (<code>&lt;</code>, <code>&amp;</code>, <code>'</code> and <code>"</code>
     * will be escaped.
     * Note: An XML processor returns normalized value that can be different.
     *
     * @param val a string to be escaped
     *
     * @return escaped value
     * @throws CharConversionException if val contains an improper XML character
     *
     * @since 1.40
     */
    public static String toAttributeValue(String val) throws CharConversionException {
        if (val == null) {
            throw new CharConversionException("null"); // NOI18N
        }

        if (checkAttributeCharacters(val)) {
            return val;
        }

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < val.length(); i++) {
            char ch = val.charAt(i);

            if ('<' == ch) {
                buf.append("&lt;");

                continue;
            } else if ('&' == ch) {
                buf.append("&amp;");

                continue;
            } else if ('\'' == ch) {
                buf.append("&apos;");

                continue;
            } else if ('"' == ch) {
                buf.append("&quot;");

                continue;
            }

            buf.append(ch);
        }

        return buf.toString();
    }

    /**
     * Escape passed string as XML element content (<code>&lt;</code>,
     * <code>&amp;</code> and <code>><code> in <code>]]></code> sequences).
     *
     * @param val a string to be escaped
     *
     * @return escaped value
     * @throws CharConversionException if val contains an improper XML character
     *
     * @since 1.40
     */
    public static String toElementContent(String val) throws CharConversionException {
        if (val == null) {
            throw new CharConversionException("null"); // NOI18N
        }

        if (checkContentCharacters(val)) {
            return val;
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < val.length(); i++) {
            char ch = val.charAt(i);

            if ('<' == ch) {
                buf.append("&lt;");

                continue;
            } else if ('&' == ch) {
                buf.append("&amp;");

                continue;
            } else if (('>' == ch) && (i > 1) && (val.charAt(i - 2) == ']') && (val.charAt(i - 1) == ']')) {
                buf.append("&gt;");

                continue;
            }

            buf.append(ch);
        }

        return buf.toString();
    }

    /**
     * Can be used to encode values that contain invalid XML characters.
     * At SAX parser end must be used pair method to get original value.
     *
     * @param val data to be converted
     * @param start offset
     * @param len count
     *
     * @since 1.29
     */
    public static String toHex(byte[] val, int start, int len) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < len; i++) {
            byte b = val[start + i];
            buf.append(DEC2HEX[(b & 0xf0) >> 4]);
            buf.append(DEC2HEX[b & 0x0f]);
        }

        return buf.toString();
    }

    /**
     * Decodes data encoded using {@link #toHex(byte[],int,int) toHex}.
     *
     * @param hex data to be converted
     * @param start offset
     * @param len count
     *
     * @throws IOException if input does not represent hex encoded value
     *
     * @since 1.29
     */
    public static byte[] fromHex(char[] hex, int start, int len)
    throws IOException {
        if (hex == null) {
            throw new IOException("null");
        }

        int i = hex.length;

        if ((i % 2) != 0) {
            throw new IOException("odd length");
        }

        byte[] magic = new byte[i / 2];

        for (; i > 0; i -= 2) {
            String g = new String(hex, i - 2, 2);

            try {
                magic[(i / 2) - 1] = (byte) Integer.parseInt(g, 16);
            } catch (NumberFormatException ex) {
                throw new IOException(ex.getLocalizedMessage());
            }
        }

        return magic;
    }

    /**
     * Check if all passed characters match XML expression [2].
     * @return true if no escaping necessary
     * @throws CharConversionException if contains invalid chars
     */
    private static boolean checkAttributeCharacters(String chars)
    throws CharConversionException {
        boolean escape = false;

        for (int i = 0; i < chars.length(); i++) {
            char ch = chars.charAt(i);

            if (((int) ch) <= 93) { // we are UNICODE ']'

                switch (ch) {
                case 0x9:
                case 0xA:
                case 0xD:

                    continue;

                case '\'':
                case '"':
                case '<':
                case '&':
                    escape = true;

                    continue;

                default:

                    if (((int) ch) < 0x20) {
                        throw new CharConversionException("Invalid XML character &#" + ((int) ch) + ";.");
                    }
                }
            }
        }

        return escape == false;
    }

    /**
     * Check if all passed characters match XML expression [2].
     * @return true if no escaping necessary
     * @throws CharConversionException if contains invalid chars
     */
    private static boolean checkContentCharacters(String chars)
    throws CharConversionException {
        boolean escape = false;

        for (int i = 0; i < chars.length(); i++) {
            char ch = chars.charAt(i);

            if (((int) ch) <= 93) { // we are UNICODE ']'

                switch (ch) {
                case 0x9:
                case 0xA:
                case 0xD:

                    continue;

                case '>': // only ]]> is dangerous

                    if (escape) {
                        continue;
                    }

                    escape = (i > 0) && (chars.charAt(i - 1) == ']');

                    continue;

                case '<':
                case '&':
                    escape = true;

                    continue;

                default:

                    if (((int) ch) < 0x20) {
                        throw new CharConversionException("Invalid XML character &#" + ((int) ch) + ";.");
                    }
                }
            }
        }
        return escape == false;
    }
}
