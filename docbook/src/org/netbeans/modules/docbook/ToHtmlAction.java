/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.docbook;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.*;
import org.xml.sax.*;
import org.xml.sax.InputSource;

public class ToHtmlAction extends CookieAction implements ErrorListener, ErrorHandler {
    
    private static final String XSL_SLIDES = "http://docbook.sourceforge.net/release/slides/current/xsl/xhtml/plain.xsl";
    
    protected Class[] cookieClasses() {
        return new Class[] {DocBookDataObject.class};
    }
    
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }
    
    private OutputWriter err;
    
    protected void performAction(Node[] nodes) {
        DocBookDataObject o = (DocBookDataObject)nodes[0].getCookie(DocBookDataObject.class);
        FileObject fo = o.getPrimaryFile();
        File f = FileUtil.toFile(fo);
        if (f == null) {
            String message = "Not a real file on disk: " + fo.getNameExt();
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        String mime = fo.getMIMEType();
        if (mime.equals(DocBookDataLoader.MIME_SLIDES)) {
            String name = fo.getName();
            InputOutput io = IOProvider.getDefault().getIO(NbBundle.getMessage(ToHtmlAction.class, "LBL_tab_db_conv"), false);
            io.setFocusTaken(true);
            err = io.getErr();
            try {
                err.reset();
                FileObject folder = fo.getParent().getFileObject(name);
                if (folder == null) {
                    folder = fo.getParent().createFolder(name);
                }
                FileObject dummy = folder.getFileObject("dummy.html");
                if (dummy == null) {
                    dummy = folder.createData("dummy.html");
                }
                File dummyF = FileUtil.toFile(dummy);
                assert dummyF != null;
                SAXParserFactory saxpf = SAXParserFactory.newInstance();
                saxpf.setNamespaceAware(true);
                XMLReader reader = saxpf.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                EntityResolver resolver = new DocBookCatalog.Reader();
                reader.setEntityResolver(resolver);
                //InputSource styleSource = new InputSource(XSL_SLIDES);
                InputSource styleSource = resolver.resolveEntity(null, XSL_SLIDES);
                assert styleSource != null;
                Source style = new SAXSource(reader, styleSource);
                //Source style = new StreamSource(XSL_SLIDES);
                TransformerFactory tf = TransformerFactory.newInstance();
                /*
                CodeSource s = tf.getClass().getProtectionDomain().getCodeSource();
                err.println("Using XSLT transformer from " + (s != null ? s.getLocation().toExternalForm() : "the JRE"));
                err.println("supports SAX sources: " + tf.getFeature(SAXSource.FEATURE));
                 */
                tf.setURIResolver(new EntityResolver2URIResolver(resolver));
                Transformer t = tf.newTransformer(style);
                t.setParameter("output.indent", "yes");
                /*XXX see below...
                t.setParameter("graphics.dir", "graphics");
                t.setParameter("script.dir", "browser");
                 */
                //t.setParameter("css.stylesheet", "stylesheet.css");
                // Setting stylesheet does not actually work:
                // https://sourceforge.net/tracker/?func=detail&aid=758093&group_id=21935&atid=373747
                saxpf.setValidating(true);
                reader = saxpf.newSAXParser().getXMLReader();
                reader.setErrorHandler(this);
                reader.setEntityResolver(resolver); // XXX include EntityCatalog.default too?
                Source source = new SAXSource(reader, new InputSource(fo.getURL().toExternalForm()));
                //Source source = new StreamSource(fo.getURL().toExternalForm());
                Result result = new StreamResult(dummyF);
                t.setErrorListener(this);
                t.transform(source, result);
                dummy.delete();
                folder.refresh();
                // XXX copy browser & graphics dirs
                // XXX subst http://docbook.sourceforge.net/release/slides/browser/slides.css with slides.css & copy
                FileObject index = folder.getFileObject("index.html");
                if (index != null) {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(index.getURL());
                }
            } catch (Exception e) {
                e.printStackTrace(err);
            } finally {
                err = null;
            }
        } else {
            assert false : mime;
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ToHtmlAction.class, "LBL_action");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
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
    
    private static final class EntityResolver2URIResolver implements URIResolver {
        private final EntityResolver resolver;
        public EntityResolver2URIResolver(EntityResolver resolver) {
            this.resolver = resolver;
        }
        public Source resolve(String href, String base) throws TransformerException {
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
    
}
