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
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Converts a DocBook XML file (currently, Slides only) to HTML.
 */
public class ToHtmlAction extends CookieAction {
    
    private static final String XSL_SLIDES = "http://docbook.sourceforge.net/release/slides/current/xsl/xhtml/plain.xsl";
    private static final String XSL_ARTICLE = "http://docbook.sourceforge.net/release/xsl/current/xhtml/docbook.xsl";
    
    protected Class[] cookieClasses() {
        return new Class[] {DocBookDataObject.class};
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    protected void performAction(Node[] nodes) {
        final DocBookDataObject o = (DocBookDataObject)nodes[0].getCookie(DocBookDataObject.class);
        RequestProcessor.getDefault().post(new Processor (o));
    }
    
    
    private static class Processor implements Runnable, ErrorListener, ErrorHandler {
        private final DocBookDataObject o;
        Processor (DocBookDataObject o) {
            this.o = o;
        }
        
        InputOutput io;
        OutputWriter err;
        OutputWriter out;
        public void run() {
            io = IOProvider.getDefault().getIO(NbBundle.getMessage(ToHtmlAction.class, "LBL_tab_db_conv"), false);            
            err = io.getErr();
            out = io.getOut();
            try {
                out.reset();
                process (o);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            } finally {
                out.close();
            }
        }
        
    private void process(DocBookDataObject o) {
        FileObject fo = o.getPrimaryFile();
        File f = FileUtil.toFile(fo);
        if (f == null) {
            String message = "Not a real file on disk: " + fo.getNameExt();
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        String mime = fo.getMIMEType();
        io.select();
        if (mime.equals(DocBookDataLoader.MIME_SLIDES)) {
            String name = fo.getName();
            try {
                // XXX #45604: throws an NPE later: out.reset();
                out.println("Initializing...");
                FileObject folder = fo.getParent().getFileObject(name);
                if (folder == null) {
                    folder = fo.getParent().createFolder(name);
                }
                FileObject dummy = folder.getFileObject("dummy.html");
                if (dummy == null) {
                    dummy = folder.createData("dummy.html");
                }
                out.println ("Will convert " + name + " to " + dummy.getPath());
                
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
                err.println("Loading stylesheet...");
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
                out.println("Processing...");
                t.transform(source, result);
                dummy.delete();
                folder.refresh();
                String lib = "org/netbeans/modules/docbook/lib/slides-" + Config.SLIDES_VERSION + "/";
                copyDir(folder, "browser/", Config.BROWSER_FILES, lib + "browser/");
                copyDir(folder, "graphics/", Config.GRAPHICS_FILES, lib + "graphics/");
                FileObject index = folder.getFileObject("index.html");
                if (index != null) {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(index.getURL());
                }
                out.println("Done.");
            } catch (Exception e) {
                e.printStackTrace(err);
            }
        } else if (mime.equals(DocBookDataLoader.MIME_DOCBOOK)) {
            String name = fo.getName();
            try {
                out.reset();
                out.println("Initializing...");
                FileObject dir = fo.getParent().getFileObject("dist");
                if (dir == null) {
                    dir = fo.getParent().createFolder("dist");
                } else if (!dir.isFolder()) {
                    throw new IOException (dir.getName() + " is not a folder");
                }
                FileObject outFile = dir.getFileObject(name, "html");
                if (outFile == null) {
                    outFile = dir.createData(name, "html");
                }
                out.println ("Will convert " + name + " to " + outFile.getPath());
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
                t.setParameter("graphics.dir", "graphics");
                saxpf.setValidating(true);
                reader = saxpf.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                reader.setEntityResolver(new ER (resolver, FileUtil.toFile(fo).getParentFile()));
                InputSource docbook = new InputSource(fo.getURL().toExternalForm());
                Source source = new SAXSource(reader, docbook);
                
                FileLock l = outFile.lock();
                out.println ("Transforming XML...");
                try {
                    OutputStream outS = outFile.getOutputStream(l);
                    try {
                        Result result = new StreamResult(outS);
                        t.setErrorListener(this);
                        t.transform(source, result);
                    } finally {
                        outS.close();
                    }
                } finally {
                    l.releaseLock();
                }
                copyGraphics (fo.getParent(), dir, io);
                HtmlBrowser.URLDisplayer.getDefault().showURL(outFile.getURL());
                out.println("Done.");
            } catch (Exception e) {
                if (err != null) {
                    err.println ("Failed.");
                    e.printStackTrace(err);
                } else {
                    ErrorManager.getDefault().notify(e);
                }
            }
        } else {
            //mime type can be bad if content is malformed
            StatusDisplayer.getDefault().setStatusText("Could not render " + 
                    fo.getPath());
        }
    }
    
    private static void copyGraphics (FileObject root, FileObject destFolder, InputOutput io) throws IOException {
        Set s = new HashSet();
        s.add ("png");
        s.add ("gif");
        s.add ("jpg");
        s.add ("jpeg");
        copyGraphics (root, destFolder, s, io);
    }
    
    private static void copyGraphics (FileObject root, FileObject destFolder, Set endings, InputOutput io) throws IOException {
        FileObject[] kids = root.getChildren();
        for (int i=0; i < kids.length; i++) {
            FileObject curr = kids[i];
            if (curr.isFolder() && !"dist".equals(curr.getName())) {
                copyGraphics (curr, destFolder, endings, io);
            } else if (curr.isData()) {
                String ext = curr.getExt().toLowerCase();
                if (endings.contains(ext)) {
                    if (destFolder.getFileObject (curr.getName(), curr.getExt()) != null) {
                        io.getErr().println ("  Not copying " + curr.getNameExt() + " to " +
                                destFolder.getPath() + " - there is already a " +
                                "file there");
                    }  else {
                        io.getOut().println("  Copy " + curr.getName() + " to " + destFolder.getPath());
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
                    } finally {
                        is.close();
                    }
                } finally {
                    os.close();
                }
            } finally {
                l.releaseLock();
            }
        }
    }
    
    public void fatalError(TransformerException exception) throws TransformerException {
        throw exception;
    }
    
    public void error(TransformerException exception) throws TransformerException {
        throw exception;
    }
    
    public void warning(TransformerException exception) throws TransformerException {
        // XXX show location etc.
        err.println(exception.getMessage());
    }
    
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public void error(SAXParseException exception) throws SAXException {
        // XXX show location etc.
        err.println(exception.getMessage());
    }
    
    public void warning(SAXParseException exception) throws SAXException {
        // XXX show location etc.
        err.println(exception.getMessage());
    }
    } //End Processor

    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return NbBundle.getMessage(ToHtmlAction.class, "LBL_action");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    private static final class EntityResolver2URIResolver implements URIResolver {
        private final EntityResolver resolver;
        public EntityResolver2URIResolver(EntityResolver resolver) {
            this.resolver = resolver;
        }
        public Source resolve(String href, String base) throws TransformerException {
            System.err.println("DO RESOLVE HREF " + href + " BASE " + base);
            try {
                String abs = new URL(new URL(base), href).toExternalForm();
                InputSource s = resolver.resolveEntity(null, abs);
                if (s != null) {
                    //err.println(href + " in " + base + " -> " + s.getSystemId());
                    return new StreamSource(s.getSystemId());
                } else {
                    //err.println(href + " in " + base + " -> zip");
                    return null;
                }
            } catch (SAXException e) {
                throw new TransformerException(e);
            } catch (IOException e) {
                throw new TransformerException(e);
            }
        }
    }

    
    private static final class ER implements EntityResolver {
        private final EntityResolver toProxy;
        private final File dir;
        ER (EntityResolver toProxy, File dir) {
            this.dir = dir;
            this.toProxy = toProxy;
        }
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            InputSource result = toProxy.resolveEntity (publicId, systemId);
            System.err.println("RESOLVE ENTITY " + publicId + "  SYSTEM " + systemId + " returns " + result);
            return result;
        }
        
    }
    
    private static final class CH implements ContentHandler {
        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
            System.err.println("Start document");
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            System.err.println("StartElement " + uri + " local name " + qName + " atts " + atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            System.err.println("CHARACTERS " + new String (ch, start, length));
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }
        
    }
}
