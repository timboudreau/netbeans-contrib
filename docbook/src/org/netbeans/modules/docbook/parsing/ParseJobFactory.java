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
/*
 * ParseJobFactory.java
 *
 * Created on October 16, 2006, 7:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.docbook.parsing;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.docbook.Callback;
import org.netbeans.api.docbook.ContentHandlerCallback;
import org.netbeans.api.docbook.ParseJob;
import org.netbeans.api.docbook.PatternCallback;
import org.netbeans.api.workqueues.Dispatcher;
import org.netbeans.api.workqueues.Drainable;
import org.netbeans.api.workqueues.QueueWorkProcessor;
import org.netbeans.modules.docbook.DocBookCatalog;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Provides a facility for hanging work off of asynchronous parses of files.
 * Any UI that needs information about the content of a file can register
 * a callback to be notified when the parse occurs.
 * <p>
 * Supports registering SAX ContentHandlers and regular expressions.
 * <p>
 * Uses a Dispatcher (see contrib/misc/workqueues) to push work onto a background
 * thread.  Work is enqueued, and batched - ParseJobs which can perform
 * some type of processing on a given file are what is enqueued.  When they
 * run, the callback is notified as processing occurs.
 * <p>
 * This makes it possible for all parts of the system that want to do some
 * processing of the XML file to do so in a single pass.
 * <p>
 * So basically, client code subclasses PatternCallback or ContentHandlerCallback.
 * These are passed here and ParseJobs for those callbacks are returned.
 * Client code enqueues the callbacks (if they were enqueued immediately they
 * could start running before the ParseJob has been returned, which is
 * undesirable - so they must be enqueued separately to guarantee the
 * client code has an instance of the ParseJob the whole time it is enqueued and
 * running, so that it can be cancelled before it runs).
 * <p>
 * The ParseJobs are pushed into the asynch work queue, which collects them
 * indexed by file.  After its delay (so multiple ParseJobs for a single
 * file can be enqueued and handled in a single pass), the work queue worker
 * thread wakes up and notices the work.  It iterates the files work is
 * queued for, passing each file and the collection of work (Drainable) to
 * the process() method on this class. There we drain the pending work for a
 * file into two Lists, one for PatternCallbacks and one for ContentHandlerCallbacks.
 * The patterns are matched against the file data and the matches are passed into
 * the handler method of the PatternCallback that originated the expression.
 * Then the ContentHandlerCallbacks' ContentHandlers are coalesced into a
 * ProxyContentHandler which runs using a SAX parser;  the content handlers
 * are notified in the standard way as the XML is parsed.  Implementations can
 * gather whatever data they need and show it to the user.
 *
 * @author Tim Boudreau
 */
public class ParseJobFactory implements QueueWorkProcessor <FileObject, ParseJob> {
    private final Dispatcher <FileObject, ParseJob> queue;
    private static final Pattern DETECT_CHAPTER_PATTERN = Pattern.compile("(<\\s*chapter.*?)+");
    private static final Pattern DETECT_ARTICLE_PATTERN = Pattern.compile("(<\\s*article.*?)+?");
    private static final Pattern DETECT_DTD_PATTERN = Pattern.compile("<\\s*!DOCTYPE.*?");
    private static final Pattern DETECT_SOLBOOK_COURSE_PATTERN = Pattern.compile("(<\\s*course.*?)+?");
    private static final Pattern DETECT_SOLBOOK_HELPSET_PATTERN = Pattern.compile("(<\\s*helpset.*?)+?");
    private static final Pattern DETECT_SOLBOOK_SLIDESET_PATTERN = Pattern.compile("(<\\s*slideset.*?)+?");
    private static final Pattern DETECT_SOLBOOK_BOOK_PATTERN = Pattern.compile("(<\\s*book.*?)+?");

    private static final String DTD_CHAPTER =
"\n<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n";

    private static final String DTD_ARTICLE =
"\n<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\" \"http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd\">\n";

    private static final String DTD_SOLBOOK_COURSE =
"\n<!DOCTYPE course PUBLIC \"-//Sun Microsystems//DTD XML-SolBook 3.5 //EN\" \"xsolbook.dtd\">\n";

    private static final String DTD_SOLBOOK_HELPSET =
"\n<!DOCTYPE helpset PUBLIC \"-//Sun Microsystems//DTD XML-SolBook 3.5 //EN\" \"xsolbook.dtd\">\n";
    
    private static final String DTD_SOLBOOK_SLIDESET =
"\n<!DOCTYPE slideset PUBLIC \"-//Sun Microsystems//DTD XML-SolBook 3.5 //EN\" \"xsolbook.dtd\">\n";
  
    private static final String DTD_SOLBOOK_BOOK =
"\n<!DOCTYPE book PUBLIC \"-//Sun Microsystems//DTD XML-SolBook 3.5 //EN\" \"xsolbook.dtd\">\n";
    

    
    
    

    public ParseJobFactory() {
        queue = new Dispatcher <FileObject, ParseJob> (this);
    }

    public static void enqueue(final ParseJob parseJob) {
        ((ParseJobImpl) parseJob).setCancelled(false);
        INSTANCE.doEnqueue ((ParseJobImpl) parseJob);
    }

    public static void enqueue (final Collection <ParseJob> jobs) {
        INSTANCE.doEnqueue(jobs);
    }

    private static final ParseJobFactory INSTANCE = new ParseJobFactory();

    private ParseJobImpl doCreateJob (final FileObject file, final Callback callback) {
        ParseJobImpl result = callback instanceof PatternCallback ?
            new PatternParseJobImpl ((PatternCallback)callback, file) :
            new ContentHandlerParseJobImpl ((ContentHandlerCallback) callback,
                file);
        return result;
    }

    public static ParseJob createJob(final FileObject file, final Callback callback) {
        return INSTANCE.doCreateJob (file, callback);
    }

    public static Collection<ParseJob> createJobs (FileObject file, Collection<Callback> callbacks) {
        return INSTANCE.doCreateJobs (file, callbacks);
    }

    private Collection<ParseJob> doCreateJobs(FileObject file, Collection<Callback> callbacks) {
        List<ParseJob> result = new ArrayList<ParseJob>(callbacks.size());
        for (Callback callback : callbacks) {
            result.add (createJob (file, callback));
        }
        return result;
    }

    public static void dequeueAll (FileObject obj) {
        Drainable<ParseJob> d = INSTANCE.queue.remove(obj);
        if (d == null) {
            return;
        }
        List <ParseJob> jobs = d.drain(ParseJob.class);
        for (ParseJob job : jobs) {
            ParseJobImpl pji = (ParseJobImpl) job;
            pji.callback.cancel();
        }
    }

    public void doEnqueue (Collection <ParseJob> jobs) {
        if (jobs.isEmpty()) return;
        ParseJobImpl one = (ParseJobImpl) jobs.iterator().next();
        FileObject file = one.file;
        for (ParseJob job : jobs) {
            ParseJobImpl pji = (ParseJobImpl) job;
            if (!pji.isCancelled()) {
                queue.put (pji.file, job);
            }
        }
    }

    public static void cancelled(final ParseJob parseJob) {
        ((ParseJobImpl) parseJob).setCancelled(true);
    }

    private void doEnqueue (final ParseJobImpl job) {
        if (!job.isCancelled()) {
            queue.put (job.file, job);
        }
    }

    public void process(FileObject file, Drainable <ParseJob> work) throws Exception {
        System.err.println("QUEUE RUNNING");

        List<PatternParseJobImpl> regexJobs = work.drain(PatternParseJobImpl.class);

        List<ContentHandlerParseJobImpl> xmlJobs = work.drain(ContentHandlerParseJobImpl.class);

        //Get the file contents
        CharSequence seq = getContent (file);
        if (seq == null) return;
        CharSequence nue = maybeInsertFakeDtd (seq);
        boolean dtdInserted = seq != nue;
        seq = nue;
        processPatternJobs (file, regexJobs, seq, dtdInserted);
        processXMLJobs (file, xmlJobs, seq, dtdInserted);
    }

    private void processPatternJobs(FileObject file, List<? extends ParseJobImpl<Callback<Pattern>>> regexJobs,
            CharSequence seq, boolean dtdInserted) throws Exception {
        for (Iterator<? extends ParseJobImpl<Callback<Pattern>>> i=regexJobs.iterator(); i.hasNext();) {
            ParseJobImpl <Callback<Pattern>> p = i.next();
            System.err.println("RUN " + p);
            p.setRunning(true);
            p.setEnqueued(false);
            i.remove();
            if (p.isCancelled()) continue;
            Callback <Pattern> pp = p.callback;
            Pattern pattern = pp.getProcessor();
            try {
                Matcher matcher = pattern.matcher(seq);
                p.doStart (pp, p.file);
                while (matcher.find()) {
                    MatchResult result = matcher.toMatchResult();
                    if (!((PatternCallback) pp).process(
                            file, result, seq)) {
                        break;
                    }
                }
            } catch (Exception e) {
                p.doFail (pp, e, file);
            } finally {
                try {
                    p.doDone (p.callback, file);
                    p.notifyFinished();
                } catch (RuntimeException e) {
                    ErrorManager.getDefault().notify (e);
                }
            }
        }
    }

    private void processXMLJobs(FileObject file, List<? extends ParseJobImpl<Callback<ContentHandler>>> xmlJobs,
            CharSequence seq, boolean dtdInserted) throws Exception {
        System.err.println("RUN XML JOBS: " + xmlJobs);
        //Now do the XML reader based ones
        if (!xmlJobs.isEmpty()) {
            SAXParserFactory f = SAXParserFactory.newInstance();

            f.setNamespaceAware(true);
            f.setValidating(true);
            SAXParser parser = f.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            System.err.println("CREATING PROXY CONTENT HANDLER FOR " + xmlJobs);
            ProxyContentHandler handler = new ProxyContentHandler (xmlJobs, file, dtdInserted);
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.setDTDHandler(handler);
            EntityResolver resolver = new DocBookCatalog.Reader();
            reader.setEntityResolver(new ER(resolver, file));

            if (reader instanceof Locator) {
                System.err.println("GOT A LOCATOR");
                handler.setDocumentLocator((Locator) reader);
            }
            handler.start();
            System.err.println("Starting SAX parse");
            try {
                reader.parse(new InputSource (new StringReader(seq.toString())));
                System.err.println("PARSE Succeeded");
            } catch (FileNotFoundException e) {
                Logger.getLogger(ParseJobFactory.class.getName()).log(Level.INFO,
                        "FNFE resolving entities?", e);
            } catch (SAXException e) {
                Logger.getLogger(ParseJobFactory.class.getName()).log(Level.INFO,
                        "Error during SAX parsing of " + file.getPath(),
                        e);
            } finally {
                handler.done();
            }
        }
    }

    private static CharSequence getContent (final FileObject ob) {
        DataObject dob;
        try {
            dob = DataObject.find(ob);
            EditorCookie ed = dob.getCookie (EditorCookie.class);
            CharSequence result;
            if (ed != null) {
                result = getSequenceFromEditor (ed, ob);
            } else {
                result = getSequenceFromFile (ob);
            }
            return result;
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }


    private static CharSequence maybeInsertFakeDtd (CharSequence seq) {
//        if (true) return seq;
        if (seq.length() > 20) {
            if (!DETECT_DTD_PATTERN.matcher(seq).find()) {
                System.err.println("FOUND NO DTD");
                if (DETECT_CHAPTER_PATTERN.matcher(seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>") + 2;
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_CHAPTER);
                    System.err.println("MAKE IT A CHAPTER: " + b);
                    return b;
                } else if (DETECT_ARTICLE_PATTERN.matcher (seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>" + 2);
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_ARTICLE);
                    System.err.println("MAKE IT AN ARTICLE:\n" + b);
                    return b;
                } else if (DETECT_SOLBOOK_BOOK_PATTERN.matcher (seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>" + 2);
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_SOLBOOK_BOOK);
                    System.err.println("MAKE IT AN ARTICLE:\n" + b);
                    return b;
                } else if (DETECT_SOLBOOK_SLIDESET_PATTERN.matcher (seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>" + 2);
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_SOLBOOK_SLIDESET);
                    System.err.println("MAKE IT AN ARTICLE:\n" + b);
                    return b;
                } else if (DETECT_SOLBOOK_COURSE_PATTERN.matcher (seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>" + 2);
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_SOLBOOK_COURSE);
                    System.err.println("MAKE IT AN ARTICLE:\n" + b);
                    return b;
                } else if (DETECT_SOLBOOK_HELPSET_PATTERN.matcher (seq).find()) {
                    StringBuilder b = new StringBuilder (seq);
                    int insertPos = 0;
                    if (b.indexOf("<?xml") >= 0) {
                        //XXX can have multiple processing instructions - find
                        //last <?.*>
                        insertPos = b.indexOf ("?>" + 2);
                        if (insertPos < 0) {
                            insertPos = 0;
                        }
                    }
                    b.insert (insertPos, DTD_SOLBOOK_HELPSET);
                    System.err.println("MAKE IT AN ARTICLE:\n" + b);
                    return b;
                }
                System.err.println("I DON'T KNOW WHAT IT IS!: " + seq.subSequence(0, Math.min (200, seq.length())));
            }
        }
        return seq;
    }

    private static CharSequence getSequenceFromEditor (final EditorCookie ck, final FileObject ob) {
        StyledDocument doc = ck.getDocument();
        if (doc == null) {
            return getSequenceFromFile (ob);
        }
        final StringFetcher fetcher = new StringFetcher(doc);
        doc.render (fetcher);
        return fetcher.get();
    }

    private static final class StringFetcher implements Runnable {
        private final StyledDocument doc;
        private String string;

        StringFetcher (StyledDocument doc) {
            this.doc = doc;
            assert doc != null;
        }

        public void run() {
            try {
                string = doc.getText(0, doc.getLength());
            } catch (BadLocationException ble) {
                ErrorManager.getDefault().notify (ble);
            }
        }

        public CharSequence get() {
            return string;
        }
    }

    private static CharSequence getSequenceFromFile (final FileObject ob) {
        if (!ob.isValid()) {
            return null;
        }
        File f = FileUtil.toFile (ob);
        if (f.length() > Integer.MAX_VALUE) {
            return null;
        }
        if (f == null) return null;
        try {
            FileInputStream stream = new FileInputStream (f);
            FileChannel channel = stream.getChannel();
            ByteBuffer buf = ByteBuffer.allocate ((int) f.length());
            try {
                channel.read (buf);
                buf.flip();
                //XXX go actually read the encoding from the file - code
                //exists in DataNode but currently broken
                CharSequence seq = Charset.forName("UTF-8").decode(buf);
                System.err.println("GOT " + seq.length() + " chars from " + ob.getName());
                return seq;
            } finally {
                channel.close();
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify (ioe);
            return null;
        }
    }

    public static void cancelled(PatternCallback patternCallback, FileObject ob) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void cancelled(ContentHandlerCallback contentHandlerCallback, FileObject ob) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static FileObject getFile(ParseJob parseJob) {
        return ((ParseJobImpl) parseJob).file;
    }

    public boolean handleException(Exception e, FileObject obj, Drainable work) {
        Logger.getLogger(ParseJobFactory.class.getName()).log (Level.INFO,
                "Exception processing work", e);
        return true;
    }

    public static abstract class ParseJobImpl <T extends Callback> extends ParseJob <Callback> {
        final T callback;
        final FileObject file;
        private volatile boolean cancelled = false;
        private volatile boolean enqueued = false;
        private volatile boolean running = false;
        private final Object lock;
        ParseJobImpl (T callback, FileObject file) {
            this.lock = new Object();
            this.callback = callback;
            this.file = file;
        }

        public String toString() {
            return "ParseJobImpl@" +
                    System.identityHashCode(this) + " for " + file.getPath() +
                    " with " + callback;
        }

        void notifyFinished() {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        public boolean isEnqueued() {
            return enqueued;
        }

        public final void waitFinished() throws InterruptedException {
            if (EventQueue.isDispatchThread()) {
                throw new IllegalStateException("Cannot wait from event " +
                        "dispatch thread");
            }
            synchronized (lock) {
                System.err.println("about to wait - enqueued " + isEnqueued() + " running " + isRunning());
                if (!isEnqueued() && !isRunning()) return;
                while (isRunning()) {
                    lock.wait(1000);
                    Map <Thread, StackTraceElement[]> m = Thread.getAllStackTraces();
                    for (Thread t : m.keySet()) {
                        StackTraceElement[] ste = m.get(t);
                        System.err.println(t);
                        for (int i=0; i < Math.min (15, ste.length); i++) {
                            System.err.println(" :" + ste[i]);
                        }
                    }
                }
            }
        }

        public final void waitFinished(long timeout) throws InterruptedException {
            if (EventQueue.isDispatchThread()) {
                throw new IllegalStateException("Cannot wait from event " +
                        "dispatch thread");
            }
            synchronized (lock) {
                if (!isEnqueued() && !isRunning()) return;
                lock.wait(timeout);
            }
        }


        public boolean isRunning() {
            return running;
        }

        void doStart (T callback, FileObject ob) {
            setRunning(true);
            start (callback, ob, this);
        }

        void doDone (T callback, FileObject ob) {
            done (callback, ob, this);
            setRunning(false);
        }

        void doFail (T c, Exception e, FileObject ob) {
            failed (c, e, ob, this);
        }

        boolean isCancelled() {
            return cancelled;
        }

        void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        void setEnqueued(boolean enqueued) {
            this.enqueued = enqueued;
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        public boolean equals (Object o) {
            return o instanceof ParseJobImpl &&
                    ((ParseJobImpl) o).callback.equals(callback);
        }

        public int hashCode() {
            return callback.hashCode() * 31;
        }
    }

    private static final class ER implements EntityResolver {
        private final EntityResolver proxy;
        private final FileObject file;
        public ER (EntityResolver proxy, FileObject file) {
            this.proxy = proxy;
            this.file = file;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            System.err.println("RESOLVE ENTITY " + publicId + " " + systemId);
            if (systemId.startsWith("file:///")) {
                System.err.println("Got a file: " + systemId);
                try {
                    File f = new File (new URL(systemId).toURI());
                    if (!f.exists()) {
                        //The SAX parser will resolve relative files in
                        //entity definitions as relative to the working
                        //directory.  Here we remap them relative to the
                        //file they came from.
                        String userdir = System.getProperty("user.dir");
                        if (f.getPath().startsWith(userdir)) {
                            String relativePath =
                                    f.getPath().substring (userdir.length());
                            File nue = new File (
                                    FileUtil.toFile(file.getParent()), relativePath);
                            System.err.println("SUBSTITUTE " + nue.getPath());
                            InputSource result = new InputSource (
                                    new BufferedInputStream(new FileInputStream(nue)));

                            return result;
                        }
                    }
                } catch (URISyntaxException e) {
                    Logger.getLogger(ParseJobFactory.class.getName()).log(Level.WARNING, null, e);
                } catch (MalformedURLException mue) {
                    Logger.getLogger(ParseJobFactory.class.getName()).log(Level.WARNING, null, mue);
                }
            }
            InputSource result = proxy.resolveEntity(publicId, systemId);
            System.err.println("RESOLVED: " + result);
            return result;
        }
    }

    /** Marker class to allow us to drain the Drainable by type on jobs */
    public final class PatternParseJobImpl extends ParseJobImpl <Callback<Pattern>> {
        PatternParseJobImpl (PatternCallback callback, FileObject file) {
            super (callback, file);
        }
    }

    /** Marker class to allow us to drain the Drainable by type on jobs */
    public final class ContentHandlerParseJobImpl extends ParseJobImpl <Callback<ContentHandler>> {
        ContentHandlerParseJobImpl (ContentHandlerCallback callback, FileObject file) {
            super (callback, file);
        }
    }
}
