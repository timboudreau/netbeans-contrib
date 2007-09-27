/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.docbook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.MatchResult;
import junit.framework.TestCase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Pattern;
import org.netbeans.api.docbook.ContentHandlerCallback;
import org.netbeans.api.docbook.ParseJob;
import org.netbeans.api.docbook.ParsingService;
import org.netbeans.api.docbook.PatternCallback;
import org.netbeans.modules.docbook.parsing.ParseJobFactory;
import org.netbeans.modules.docbook.parsing.ParsingServiceImpl;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MIMEResolver;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataLoaderPool;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Tim Boudreau
 */
public class ParseJobFactoryTest extends TestCase {

    public ParseJobFactoryTest(String testName) {
        super(testName);
    }

    static {

            DocBookCatalog.PUBLIC_2_SYSTEM.put("-//Norman Walsh//DTD Slides XML V" + Config.SLIDES_VERSION + "//EN",
                                resUrl("lib/slides-" + Config.SLIDES_VERSION + "/schema/dtd/slides.dtd"));
            DocBookCatalog.PUBLIC_2_SYSTEM.put("-//OASIS//DTD DocBook XML V" + Config.DOCBOOK_XML_VERSION + "//EN",
                                resUrl ("lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/docbookx.dtd"));
            DocBookCatalog.PUBLIC_2_SYSTEM.put("-//OASIS//DTD DocBook XML V4.4//EN",
                                resUrl ("lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/docbookx.dtd"));
            // XXX slides-full.dtd

            DocBookCatalog.SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/xsl/current/",
                                resUrl("lib/docbook-xsl-" + Config.DOCBOOK_XSL_VERSION + "/"));
            DocBookCatalog.SYSTEM_2_SYSTEM.put("http://www.oasis-open.org/docbook/xml/" + Config.DOCBOOK_XML_VERSION + "/",
                                resUrl("lib/docbook-xml-" + Config.DOCBOOK_XML_VERSION + "/"));
            DocBookCatalog.SYSTEM_2_SYSTEM.put("http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl",
                                resUrl("lib/slides-" + Config.SLIDES_VERSION + "/"));
    }

    private static String resUrl (String loc) {
        URL u = ParseJobFactoryTest.class.getResource(loc);
        if (u == null) {
            throw new MissingResourceException(loc, null, null);
        }
        return u.toString();
    }

    File dataDir;
    LocalFileSystem fs;
    FileObject xml;
    ParsingService service;
    protected void setUp() throws Exception {
        System.err.println("SYSTEM PROPS: " + System.getProperties());
        System.err.println("RESOURCE XMP: " + ParseJobFactory.class.getResource("DocBookCatalog.class"));

        File tmp = new File (System.getProperty ("java.io.tmpdir"));
        dataDir = new File (tmp, "docbooktestdata" + System.currentTimeMillis());
        if (dataDir.exists()) {
            dataDir.delete();
        }
        dataDir.mkdir();
        fs = new LocalFileSystem ();
        fs.setRootDirectory(dataDir);
        xml = fs.getRoot().createData("article.xml");

        InputStream in = new BufferedInputStream (ParseJobFactoryTest.class.getResourceAsStream("ArticleTestData1.xml"));
        FileLock lock = xml.lock();
        OutputStream out = new BufferedOutputStream (xml.getOutputStream(lock));
        try {
            FileUtil.copy (in, out);
        } finally {
            out.close();
            in.close();
            lock.releaseLock();
        }
        assertTrue ("Failed to copy test xml data", xml.getSize() > 0);
//        fs.setReadOnly(true);
        service = new ParsingServiceImpl (xml);
    }

    protected void tearDown() throws Exception {
        fs = null;
        dataDir.delete();
    }

    public void testSanity() throws Exception {
        System.out.println("testSanity");

        PCallbackImpl pcallback = new PCallbackImpl();
        assertFalse (pcallback.isCancelled());
        System.err.println("enqueuing pjob");
        ParseJob pjob = service.enqueue(pcallback);
        System.err.println("pjob enqueued");
//        assertTrue (pjob.isEnqueued());

        CHCallbackImpl ccallback = new CHCallbackImpl();
        assertFalse (ccallback.isCancelled());
        System.err.println("enqueueing cjob");
        ParseJob cjob = service.enqueue(ccallback);
        System.err.println("cjob enqueued");
//        assertTrue (cjob.isEnqueued());

        Thread.currentThread().sleep (12000);
        System.err.println("Start wait ");
        pjob.waitFinished();
        cjob.waitFinished();
        System.err.println("end wait");

        pcallback.assertMatched();
        ccallback.assertNotEmpty();
        ccallback.assertCalled("startElement");
        ccallback.assertCalled("endElement");
    }

    public void testOldCallbackIsNotRerunEternally() throws Exception {
        System.out.println("testOldCallbackIsNotRerunEternally");

        PCallbackImpl pcallback = new PCallbackImpl();
        assertFalse (pcallback.isCancelled());
        System.err.println("enqueuing pjob");
        ParseJob pjob = service.enqueue(pcallback);
        System.err.println("pjob enqueued");
//        assertTrue (pjob.isEnqueued());

        CHCallbackImpl ccallback = new CHCallbackImpl();
        assertFalse (ccallback.isCancelled());
        System.err.println("enqueueing cjob");
        ParseJob cjob = service.enqueue(ccallback);
        System.err.println("cjob enqueued");
//        assertTrue (cjob.isEnqueued());

        Thread.currentThread().sleep (12000);
        System.err.println("Start wait ");
        pjob.waitFinished();
        cjob.waitFinished();
        System.err.println("end wait");

        ccallback.clear();
        pcallback.clear();

        PCallbackImpl pcallback2 = new PCallbackImpl();
        CHCallbackImpl ccallback2 = new CHCallbackImpl();
        ParseJob pjob2 = service.enqueue(pcallback);
        ParseJob cjob2 = service.enqueue(ccallback);
        pjob2.waitFinished();
        cjob2.waitFinished();
        Thread.currentThread().sleep (12000);

        ccallback.assertEmpty();
        pcallback.assertNotMatched();
    }


//    public void testEnqueue() {
//        System.out.println("enqueue");
//
//        ParseJob parseJob = null;
//
//        ParseJobFactory.enqueue(parseJob);
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public void testCreateJob() {
//        System.out.println("createJob");
//
//        FileObject file = null;
//        Callback callback = null;
//
//        ParseJob expResult = null;
//        ParseJob result = ParseJobFactory.createJob(file, callback);
//        assertEquals(expResult, result);
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of createJobs method, of class org.netbeans.modules.docbook.ParseJobFactory.
//     */
//    public void testCreateJobs() {
//        System.out.println("createJobs");
//
//        FileObject file = null;
//        Collection<Callback> callbacks = null;
//
//        Collection<ParseJob> expResult = null;
//        Collection<ParseJob> result = ParseJobFactory.createJobs(file, callbacks);
//        assertEquals(expResult, result);
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public void testDoEnqueue() {
//        System.out.println("doEnqueue");
//
//        Collection<ParseJob> jobs = null;
//        ParseJobFactory instance = new ParseJobFactory();
//
//        instance.doEnqueue(jobs);
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public void testCancelled() {
//        System.out.println("cancelled");
//
//        ParseJob parseJob = null;
//
//        ParseJobFactory.cancelled(parseJob);
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("<title>\\s*(.*)\\s*</title>"); //NOI18N

    //<?xml version="1.0" encoding="UTF-8"?>
    private static final String CONTENT_TYPE_PATTERN =
            ".*<\\?xml.*?\\s*encoding=\\s*\\\"(.*?)\\\".*?>"; //NOI18N

    private class PCallbackImpl extends PatternCallback {
        public PCallbackImpl() {
            super (TITLE_PATTERN);
        }

        public PCallbackImpl (Pattern p) {
            super (p);
        }

        public List <W> l = new ArrayList<W>();
        private int count = 0;

        boolean processReturnValue = true;

        public boolean process(FileObject f, MatchResult match, CharSequence content) {
            count++;
            System.out.println("  Process " + f.getName() + " match " + match);
            try {
                //Make sure the test method will return from enqueue before the job
                //has finished - it theoretically return earlier than that
                Thread.currentThread().sleep (20);
            } catch (InterruptedException ex) {

            }
            l.add (new W (f, match, content));
            return processReturnValue;
        }

        public void clear() {
            l.clear();
            count = 0;
        }

        public void assertMatched() {
            assertTrue (!l.isEmpty());
        }

        public void assertNotMatched() {
            assertFalse (!l.isEmpty());
        }


        private class W {
            public FileObject ob;
            public MatchResult match;
            public CharSequence content;
            public W (FileObject f, MatchResult match, CharSequence content) {
                this.ob = f; this.match = match; this.content = content;
            }
        }


    }

    private class CHCallbackImpl extends ContentHandlerCallback <ContentHandler> {
        final H h;
        public CHCallbackImpl () {
            super (new H());
            h = (ParseJobFactoryTest.H) super.getProcessor();
        }

        public Object assertCalled (String method) {
            return h.assertCalled (method);
        }

        public void assertNotEmpty() {
            h.assertNotEmpty();
        }

        public void assertNotCalled (String method) {
            h.assertNotCalled (method);
        }

        public void assertCalledWith (String method, Object args) {
            h.assertCalledWith (method, args);
        }

        public void assertEmpty() {
            h.assertEmpty();
        }
        
        public void clear() {
            h.clear();
        }
    }

    private static class H implements ContentHandler, DTDHandler, ErrorHandler {
        final Map <String, Object[]> m = new HashMap <String, Object[]> ();

        public Object assertCalled (String method) {
            Object o = m.remove (method);
            assertNotNull (method + " was not called", o);
            return o;
        }
        
        public void clear() {
            m.clear();
        }

        public void assertNotEmpty() {
            assertFalse (m.isEmpty());
        }

        public void assertNotCalled (String method) {
            assertNull (method + " should not have been called but was", m.get(method));
        }

        public void assertEmpty() {
            assertTrue (m.isEmpty());
        }

        public void assertCalledWith (String method, Object... args) {
            Object[] o = m.get(method);
            assertNotNull (o);
            assertEquals (o.length, args.length);
            for (int i = 0; i < args.length; i++) {
                Object a = args[i];
                Object b = o[i];
                assertEquals ("Argument " + i + " to method " + method + " should " +
                        "be " + a + " but was " + b, a, b);
            }
        }

        private void called (Object... args) {
            if (args.length == 0) { args = new Object[] { Boolean.TRUE }; }
            Exception e = new Exception();
            e.fillInStackTrace();
            StackTraceElement[] ste = e.getStackTrace();
            StackTraceElement caller = ste[1];
            String method = caller.getMethodName();
            System.err.println("CALLED: " + method);
            m.put (method, args);
            System.out.println("CALLED: " + method + " with " + Arrays.asList (args));
        }

        public void setDocumentLocator(Locator locator) {
            called (locator);
        }

        public void startDocument() throws SAXException {
            called();
        }

        public void endDocument() throws SAXException {
            called();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            called (prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            called (prefix);
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            called (uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            called (uri, localName, qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            called (ch, start, length);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            called (ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            called (target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            called(name);
        }

        public void notationDecl(String name, String publicId, String systemId) throws SAXException {
            called(name, publicId, systemId);
        }

        public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
            called (name, publicId, systemId, notationName);
        }

        public void warning(SAXParseException exception) throws SAXException {
            called (exception);
        }

        public void error(SAXParseException exception) throws SAXException {
            called (exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            called (exception);
        }
    }

    private static DocBookDataLoader LOADR = new DocBookDataLoader();
    public static class LKP extends ProxyLookup {
        final InstanceContent content = new InstanceContent();
        final AbstractLookup lkp = new AbstractLookup (content);
        public LKP () {
            try {
                XMLFileSystem xfs = new XMLFileSystem (ValidationAction.class.getResource("layer.xml").toURI().toString());
                content.set (Arrays.asList(new DLP(), xfs, new MR(), LOADR), null);
            } catch (URISyntaxException ex) {
                throw new IllegalStateException (ex);
            } catch (SAXException ex) {
                throw new IllegalStateException (ex);
            }
            setLookups(lkp, Lookups.metaInfServices(
                    ValidationAction.class.getClassLoader()));
        }
    }

    public static final class MR extends MIMEResolver {
        public String findMIMEType(FileObject fo) {
            if ("xml".equals (fo.getExt())) {
                return "x-docbook+xml";
            }
            return null;
        }
    }

    private static final class DLP extends DataLoaderPool {
        protected Enumeration<DataLoader> loaders() {
            return new Vector(Collections.singleton(LOADR)).elements();
        }

        Map <FileObject, DocBookDataObject> objs = new HashMap <FileObject, DocBookDataObject>();
        public DataObject findDataObject(FileObject fo) throws IOException {
            DataObject result;
            if ("xml".equals (fo.getExt())) {
                result = objs.get (fo);
                if (result == null) {
                    result = new DocBookDataObject (fo, LOADR);
                    objs.put (fo, (DocBookDataObject)result);
                }
            } else {
                result = super.findDataObject(fo);
            }
            return result;
        }

        public DataObject findDataObject(FileObject fo, DataLoader.RecognizedFiles r) throws IOException {
            DataObject result;
            if ("xml".equals (fo.getExt())) {
                result = objs.get (fo);
                if (result == null) {
                    result = new DocBookDataObject (fo, LOADR);
                    objs.put (fo, (DocBookDataObject)result);
                }
            } else {
                result = super.findDataObject(fo, r);
            }
            return result;
        }


    }

}
