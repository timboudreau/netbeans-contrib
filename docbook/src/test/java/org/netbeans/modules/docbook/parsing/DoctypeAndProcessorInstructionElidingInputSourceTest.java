/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docbook.parsing;

import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import org.openide.filesystems.FileUtil;
import static org.netbeans.modules.docbook.parsing.DoctypeAndProcessorInstructionElidingInputSource.*;

/**
 *
 * @author Tim Boudreau
 */
public class DoctypeAndProcessorInstructionElidingInputSourceTest {
    private static final String WITH_PROCESSING_AND_DOCTYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //NOI18N
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N
    
    private static final String WITH_PROCESSING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_DOCTYPE =
            "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_NOTHING =
            "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //NOI18N
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.3//EN\" \"http://www.oasis-open.org/docbook/xml/4.3/docbookx.dtd\"[<!ENTITY Monkeys SYSTEM \"Monkeys.xml\"><!ENTITY Fleas SYSTEM \"Fleas.xml\"><!ENTITY Satan SYSTEM \"Satan.xml\">]>" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_PROCESSING_AND_DOCTYPE_MULTILINE = "<?xml version=\"1.0\" \nencoding=\"UTF-8\"?>\n" //NOI18N
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \n \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_PROCESSING_MULTILINE = "<?xml version=\"1.0\" \nencoding=\"UTF-8\"?>" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_DOCTYPE_MULTILINE =
            "<!DOCTYPE book \n PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \n \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static final String WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES_MULTILINE = "<?xml version=\"1.0\" \nencoding=\"UTF-8\"?>\n" //NOI18N
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \n \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">" //NOI18N
            + "<book><chapter id=\"one\"><para>abc</para><section id=\"foo\"><title>Foo</title><para>foo</para></section></chapter></book>"; //NOI18N

    private static InputStream in(String s) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(s.getBytes("UTF-8"));
    }
    private Set<File> files = new HashSet<File>();

    private File file(String content) throws IOException {
        File f = File.createTempFile("" + System.currentTimeMillis(), ".txt");
        FileOutputStream out = new FileOutputStream(f);
        try {
            InputStream in = in(content);
            FileUtil.copy(in, out);
            in.close();
        } finally {
            out.close();
        }
        files.add(f);
        return f;
    }

    private Reader reader(String content) throws IOException {
        File f = file(content);
        DoctypeAndProcessorInstructionElidingInputSource in = new DoctypeAndProcessorInstructionElidingInputSource(f, "UTF-8");
        return in.getCharacterStream();
    }

    private String reread(String content) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = reader(content);
        for (int val = r.read(); val != -1; val = r.read()) {
            sb.append((char) val);
        }
        r.close();
        return sb.toString();
    }

    @After
    public void tearDown() throws InterruptedException {
        //Allow unclosed from InputSources streams to be GCd
        Thread.sleep(300);
        for (int i = 0; i < 5; i++) {
            System.gc();
            System.runFinalization();
        }
        for (File f : files) {
            assertTrue("Could not delete " + f.getAbsolutePath(), f.delete());
        }
    }

    @Test
    public void testProcessing() throws Exception {
        String s = reread(WITH_PROCESSING);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testClean() throws Exception {
        String s = reread(WITH_NOTHING);
        assertEquals(WITH_NOTHING, s);
    }
    
    @Test
    public void testDoctype() throws Exception {
        String s = reread(WITH_DOCTYPE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testProcessingAndDoctype() throws Exception {
        String s = reread(WITH_PROCESSING_AND_DOCTYPE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testProcessingMultiline() throws Exception {
        String s = reread(WITH_PROCESSING_MULTILINE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testDoctypeMultiline() throws Exception {
        String s = reread(WITH_DOCTYPE_MULTILINE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testProcessingAndDoctypeMultiline() throws Exception {
        String s = reread(WITH_PROCESSING_AND_DOCTYPE_MULTILINE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testWithEncodingRegex() {
        Matcher m = XML_DEF_PATTERN_WITH_ENCODING.matcher(WITH_PROCESSING_AND_DOCTYPE);
        assertTrue(m.find());
        assertEquals("UTF-8", m.group(1));
        m = XML_DEF_PATTERN_WITH_ENCODING.matcher(WITH_PROCESSING);
        assertTrue(m.find());
        assertEquals("UTF-8", m.group(1));
    }

    @Test
    public void testProcessingRegex() {
        Matcher m = XML_DEF_PATTERN.matcher(WITH_PROCESSING_AND_DOCTYPE);
        assertTrue(m.find());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length(), m.end(0));
        m = XML_DEF_PATTERN.matcher(WITH_PROCESSING);
        assertTrue(m.find());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length(), m.end(0));
    }

    @Test
    public void testDoctypeRegex() {
        Matcher m = DOCTYPE_PATTERN.matcher("<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">");
        assertTrue (m.find());
        m = DOCTYPE_PATTERN.matcher(WITH_DOCTYPE);
        assertTrue (m.find());
        m = DOCTYPE_PATTERN.matcher(WITH_PROCESSING_AND_DOCTYPE);
        assertTrue(m.find());
        assertEquals(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //NOI18N
                + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">").length(), m.end(0));
    }

    @Test
    public void testProcessingAndDoctypeWithEntities() throws Exception {
        String s = reread(WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testProcessingAndDoctypeWithEntitiesMultiline() throws Exception {
        String s = reread(WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES_MULTILINE);
        assertEquals(WITH_NOTHING, s);
    }

    @Test
    public void testDoctypeRegexWithEntities() {
        int len = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.3//EN\" \"http://www.oasis-open.org/docbook/xml/4.3/docbookx.dtd\"[<!ENTITY Monkeys SYSTEM \"Monkeys.xml\"><!ENTITY Fleas SYSTEM \"Fleas.xml\"><!ENTITY Satan SYSTEM \"Satan.xml\">]>".length();
        Matcher m = DOCTYPE_PATTERN.matcher(WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES);
        assertTrue (m.find());
        assertEquals(len, m.end());
    }

    @Test
    public void testDoctypeRegexWithEntitiesMultiline() {
        String s = "<?xml version=\"1.0\" \nencoding=\"UTF-8\"?>\n" //NOI18N
            + "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \n \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">";
        int len = s.length();
        Matcher m = DOCTYPE_PATTERN.matcher(WITH_PROCESSING_AND_DOCTYPE_AND_ENTITIES_MULTILINE);
        assertTrue (m.find());
        assertEquals(len, m.end());
    }
}
