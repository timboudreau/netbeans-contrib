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
package org.netbeans.modules.docbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.api.docbook.OutputWindowStatus;
import org.netbeans.api.docbook.Renderer;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

class Processor implements Runnable, ErrorListener, ErrorHandler {
    private static final String XSL_SLIDES = "http://docbook.sourceforge.net/release/slides/current/xsl/xhtml/plain.xsl";
    private static final String XSL_ARTICLE = "http://docbook.sourceforge.net/release/xsl/current/xhtml/docbook.xsl";
    private final FileObject destFolder;
    private final DocBookDataObject o;
    private final Renderer.JobStatus status;
    Processor(DocBookDataObject o) {
        this(o, null, null);
    }

    Processor(DocBookDataObject o, FileObject destFolder, Renderer.JobStatus status) {
        this.o = o;
        assert o != null;
        this.destFolder = destFolder;
        this.status = status == null ? new OutputWindowStatus("Converting " + 
                o.getName()) : status;
    }

    public void run() {
        process (o, destFolder);
    }

    private void process(DocBookDataObject o, FileObject destFolder) {
        FileObject fo = o.getPrimaryFile();
        File f = FileUtil.toFile(fo);
        if (f == null) {
            String message = "Not a real file on disk: " + fo.getNameExt();
            DialogDisplayer.getDefault().notify(new Message(message, NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        String mime = fo.getMIMEType();
        if (mime.equals(DocBookDataLoader.MIME_SLIDES)) {
            String name = fo.getName();
            try {
                // XXX #45604: throws an NPE later: out.reset();
                status.started("Initializing...");
                FileObject folder = destFolder == null ?
                    fo.getParent().getFileObject(name) : destFolder;

                if (folder == null) {
                    folder = fo.getParent().createFolder(name);
                }
                FileObject dummy = folder.getFileObject("dummy.html");
                if (dummy == null) {
                    dummy = folder.createData("dummy.html");
                }
                status.progress("Will convert " + name + " to " + dummy.getPath());

                File dummyF = FileUtil.toFile(dummy);
                assert dummyF != null;
                SAXParserFactory saxpf = SAXParserFactory.newInstance();
                saxpf.setNamespaceAware(true);
                XMLReader reader = saxpf.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                EntityResolver resolver = new DocBookCatalog.Reader();
                reader.setEntityResolver(resolver);
                InputSource styleSource = resolver.resolveEntity(null, XSL_SLIDES);
                assert styleSource != null;
                Source style = new SAXSource(reader, styleSource);
                TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();
                tf.setURIResolver(new EntityResolver2URIResolver(resolver));
                status.progress("Loading stylesheet...");
                Transformer t = tf.newTransformer(style);
                t.setParameter("output.indent", "yes");
                t.setParameter("graphics.dir", "graphics");
                t.setParameter("script.dir", "browser");
                t.setParameter("css.stylesheet.dir", "browser");
                // t.setParameter("css.stylesheet", "whatever.css");
                // Information on changing stylesheet (requires Slides 3.2.0+):
                // https://sourceforge.net/tracker/?func=detail&aid=758093&group_id=21935&atid=373747
                saxpf.setValidating(true);
                reader = saxpf.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                reader.setEntityResolver(resolver); // XXX include EntityCatalog.default too?
                Source source = new SAXSource(reader, new InputSource(fo.getURL().toExternalForm()));
                Result result = new StreamResult(dummyF);
                t.setErrorListener(this);
                status.progress("Processing...");
                t.transform(source, result);
                dummy.delete();
                folder.refresh();
                String lib = "org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/";
                copyDir(folder, "browser/", Config.BROWSER_FILES, lib + "browser/");
                copyDir(folder, "graphics/", Config.GRAPHICS_FILES, lib + "graphics/");
                FileObject index = folder.getFileObject("index.html");
                if (index != null) {
//                    URLDisplayer.getDefault().showURL(index.getURL());
                    status.finished("Done.", FileUtil.toFile(index));
                } else {
                    status.failed(new IllegalStateException(
                            "Failed. No file created."));
                }
            }  catch (Exception e) {
                status.failed (e);
            }
        }  else if (mime.equals(DocBookDataLoader.MIME_DOCBOOK)) {
                String name = fo.getName();
                try {
                    status.started("Initializing...");
                    FileObject dir = destFolder;
                    if (dir == null) {
                        dir = fo.getParent().getFileObject("dist");
                        if (dir == null) {
                            dir = fo.getParent().createFolder("dist");
                        } else if (!dir.isFolder()) {
                                throw new IOException(dir.getName() + " is not a folder");
                        }
                    } else {
                        dir = destFolder;
                    }
                    FileObject outFile = dir.getFileObject(name, "html");
                    if (outFile == null) {
                        outFile = dir.createData(name, "html");
                    }
                    status.progress("Will convert " + name + " to " + outFile.getPath());
                    SAXParserFactory saxpf = SAXParserFactory.newInstance();
                    saxpf.setNamespaceAware(true);
                    XMLReader reader = saxpf.newSAXParser().getXMLReader();
                    reader.setErrorHandler(this);
                    EntityResolver resolver = new DocBookCatalog.Reader();
                    reader.setEntityResolver(resolver);
                    InputSource styleSource = resolver.resolveEntity(null, XSL_ARTICLE);
                    assert styleSource != null;
                    Source style = new SAXSource(reader, styleSource);
                    TransformerFactory tf = new net.sf.saxon.TransformerFactoryImpl();
                    tf.setURIResolver(new EntityResolver2URIResolver(resolver));
                    Transformer t = tf.newTransformer(style);
                    t.setParameter("output.indent", "yes");
                    saxpf.setValidating(true);
                    reader = saxpf.newSAXParser().getXMLReader();
                    reader.setErrorHandler(this);
                    reader.setEntityResolver(resolver);
                    InputSource docbook = new InputSource(fo.getURL().toExternalForm());
                    Source source = new SAXSource(reader, docbook);

                    FileLock l = outFile.lock();
                    status.progress("Transforming XML...");
                    try {
                        OutputStream outS = outFile.getOutputStream(l);
                        try {
                            Result result = new StreamResult(outS);
                            t.setErrorListener(this);
                            t.transform(source, result);
                        }  finally {
                            outS.close();
                        }
                    }  finally {
                        l.releaseLock();
                    }
                    copyGraphics (fo.getParent(), dir, status);
                    URLDisplayer.getDefault().showURL(outFile.getURL());
                    status.finished("Done.", FileUtil.toFile(outFile));
                }  catch (Exception e) {
                    status.failed(e);
                }
            }  else {
                //mime type can be bad if content is malformed
                status.failed(new IllegalStateException ("Content malformed"));
            }
    }

    private static void copyGraphics(FileObject root, FileObject destFolder, Renderer.JobStatus status) throws IOException {
        Set s = new HashSet();
        s.add ("png");
        s.add ("gif");
        s.add ("jpg");
        s.add ("jpeg");
        copyGraphics (root, destFolder, s, status);
    }

    private static void copyGraphics(FileObject root, FileObject destFolder, Set endings, Renderer.JobStatus status) throws IOException {
        FileObject[] kids = root.getChildren();
        for (int i = 0; i < kids.length; i++) {
            FileObject curr = kids[i];
            if (curr.isFolder() && !"dist".equals(curr.getName())) {
                copyGraphics (curr, destFolder, endings, status);
            } else if (curr.isData()) {
                String ext = curr.getExt().toLowerCase();
                if (endings.contains(ext)) {
                    if (destFolder.getFileObject (curr.getName(), curr.getExt()) != null) {
                        status.warn ("  Not copying " + curr.getNameExt() + " to " +
                                destFolder.getPath() + " - there is already a " +
                                "file there");
                    }  else {
                        status.progress("  Copy " + 
                                curr.getName() + " to " + destFolder.getPath());
                        FileUtil.copyFile(curr, destFolder, curr.getName());
                    }
                }
            }
        }
    }

    /**
     * Copy some resource files to the output directory.
     * @param folder the folder to copy things to
     * @param prefix a prefix (/-separated) in that folder
     * @param files a list of files (/-separated) to put in that folder, modified by prefix
     * @param resourcePrefix a location in this JAR to get files from
     */
    private static void copyDir(FileObject folder, String prefix, String[] files, String resourcePrefix) throws IOException {
        for (int i = 0; i < files.length; i++) {
            String fname = prefix + files[i];
            FileObject fo = FileUtil.createData(folder, fname);
            FileLock l = fo.lock();
            try {
                OutputStream os = fo.getOutputStream(l);
                try {
                    String res = resourcePrefix + files[i];
                    InputStream is = ToHtmlAction.class.getClassLoader().getResourceAsStream(res);
                    assert is != null : res;
                    try {
                        FileUtil.copy(is, os);
                    }  finally {
                        is.close();
                    }
                }  finally {
                    os.close();
                }
            }  finally {
                l.releaseLock();
            }
        }
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        status.failed(exception);
        throw exception;
    }

    public void error(TransformerException exception) throws TransformerException {
        status.warn (exception.getMessageAndLocation());
        throw exception;
    }

    public void warning(TransformerException exception) throws TransformerException {
        status.warn(exception.getMessageAndLocation());
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void error(SAXParseException exception) throws SAXException {
        status.failed(exception);
    }

    public void warning(SAXParseException exception) throws SAXException {
        status.warn(exception.getMessage());
    }
}
