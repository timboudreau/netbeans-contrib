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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
 * Import.java
 *
 * Created on February 13, 2007, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.visualweb.project.importpage;

import com.sun.rave.faces.data.DefaultSelectItemsArray;
import org.netbeans.modules.visualweb.designer.html.HtmlAttribute;
import org.netbeans.modules.visualweb.designer.html.HtmlTag;
import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.DesignContext;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.markup.MarkupDesignBean;
import com.sun.rave.designtime.markup.MarkupPosition;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.faces.component.UIForm;
import javax.faces.component.UIGraphic;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import org.netbeans.modules.visualweb.api.designer.cssengine.CssProvider;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData.UrlResourceData;
import org.netbeans.modules.visualweb.api.designer.cssengine.ResourceData.UrlStringsResourceData;
import org.netbeans.modules.visualweb.api.designer.cssengine.XhtmlCss;
import org.netbeans.modules.visualweb.api.designer.markup.MarkupService;
import org.netbeans.modules.visualweb.api.designerapi.DesignerServiceHack;
import org.netbeans.modules.visualweb.api.insync.InSyncService;
import org.netbeans.modules.visualweb.insync.UndoEvent;
import org.netbeans.modules.visualweb.insync.beans.Bean;
import org.netbeans.modules.visualweb.insync.faces.FacesBean;
import org.netbeans.modules.visualweb.insync.faces.MarkupBean;
import org.netbeans.modules.visualweb.insync.live.BeansDesignBean;
import org.netbeans.modules.visualweb.insync.live.LiveUnit;
import org.netbeans.modules.visualweb.insync.markup.MarkupUnit;
import org.netbeans.modules.visualweb.insync.models.FacesModel;
import org.netbeans.modules.visualweb.project.importpage.ImportPagePanel.StringBufferOutputStream;
import org.netbeans.modules.visualweb.project.jsf.api.AddResourceOverwriteDialog;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
import org.w3c.dom.NodeList;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.tidy.DOMDocumentImpl;
import org.w3c.tidy.Tidy;

/**
 * This was originally all implemented int ImportPagePanel.  In an effort to do a little refactoring I am pulling it apart.
 *
 * @author Tor Norbye
 * refactoring by Joelle Lam
 */
public class Import {
    
    ImportContext context;
    
    /** Creates a new instance of Import */
    public Import() {
    }
    
    private boolean convert = false;
    private int encoding;
    private boolean importImages;
    private boolean convertForms;
    
    /** The actual import method. Will create the web forms etc. */
    protected void doImport(String name, URL url, boolean includeResources, boolean convert, int encoding, boolean importImages, boolean convertForms, ImportContext context) {
        this.context = context;
        this.convert = convert;
        this.encoding = encoding;
        this.importImages = importImages;
        this.convertForms = convertForms;
        
        if (context.project == null) {
            return;
        }
        
        // Create files
        try {
            DataObject webroot = null;
            context.webformFile = DesignerServiceHack.getDefault().getCurrentFile();
            
            //            if (context.webformFile == null) {
            //                // XXX TODO I should ensure that I can pass in a null parent here
            //                // (to JsfProjectUtils.addResource) to place resources at the document
            //                // root!
            //            }
            
            try {
                webroot = DataObject.find(JsfProjectUtils.getDocumentRoot(context.project));
            } catch (DataObjectNotFoundException dnfe) {
                ErrorManager.getDefault().notify(dnfe);
            }
            
            context.copyResources = includeResources;
            
            DataFolder folderObj = (DataFolder)webroot;
            FileSystem fs = (FileSystem) Repository.getDefault().getDefaultFileSystem();
            
            String tmpl;
            // XXX ToDo: These templates are for JSF 1.1 project, should consider JSF 1.2 as well.
            if (context.fragment) {
                tmpl = "Templates/JSP_Servlet/PageFragment.jspf"; // NOI18N
            } else {
                tmpl = "Templates/JSP_Servlet/Page.jsp"; // NOI18N
            }
            FileObject fo = fs.findResource(tmpl);
            
            if (fo == null) {
                throw new IOException("Can't find template FileObject for " + tmpl); // NOI18N
            }
            
            DataObject webformTemplate = DataObject.find(fo);
            DataObject webformDobj = webformTemplate.createFromTemplate(folderObj, name);
            context.webformDobj = webformDobj;
            context.webformFile = webformDobj.getPrimaryFile();
            
            // Now go and edit the heck out of it
            org.netbeans.modules.visualweb.insync.Util.retrieveDocument(webformDobj.getPrimaryFile(), true);
            
            // XXX TODO grab atomic lock
            Document cleanDoc = cleanup(url);
            context.parsedDocument = cleanDoc;
            
            if (cleanDoc == null) {
                return;
            }
            
            MarkupService.markJspxSource(cleanDoc);
            
            FacesModel model = FacesModel.getInstance(context.webformFile);
            if(model == null) {
                return;
            }
            //            WebForm webform = DesignerUtils.getWebForm(webformDobj, true);
            //
            //            if (webform == null) {
            //                return;
            //            }
            //
            //            context.webform = webform;
            
            // The rest of the import deals with insync models and has to happen
            // on the AWT thread (insync requirement)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        // The cursor change doesn't seem to have any effect:
                        //setCursor(org.openide.util.Utilities.createProgressCursor(ImportPagePanel.this));
                        
                        finishImportSafely();
                    } finally {
                        //setCursor(null);
                        clearContext();
                    }
                }
            });
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            
            String message = NbBundle.getMessage(ImportPagePanel.class, "ImportFailed"); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }
    
    
    private void clearContext() {
        context = null;
    }
    
    /** Given a URL to an online document or a file, read the file, parse it,
     * translate resource strings (and import the resources into the project in the process)
     * and returned the cleaned up dom contents.
     */
    private Document cleanup(URL resourceURL) {
        InputStream is = null;
        
        try {
            is = resourceURL.openStream();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            
            String message =
                    NbBundle.getMessage(ImportPagePanel.class, "URLAccessFailed", resourceURL.toString()); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            
            return null;
        }
        
        try {
            String name = resourceURL.toString().toLowerCase();
            boolean isJsp =
                    name.endsWith(".jsp") || name.endsWith(".jspx") || // NOI18N
                    name.endsWith(".jspf"); // NOI18N
            Tidy tidy = getTidy(isJsp, encoding);
            Document dom = rewrite(tidy, is);
            
            if (dom == null) {
                return null;
            }
            
            Document target = null;
            
            try {
                org.xml.sax.InputSource is2 =
                        new org.xml.sax.InputSource(new StringReader(
                        "<jsp:root version=\"1.2\" xmlns:f=\"http://java.sun.com/jsf/core\" " +
                        "xmlns:h=\"http://java.sun.com/jsf/html\" xmlns:jsp=\"http://java.sun.com/JSP/Page\"><jsp:directive.page contentType=\"text/html;charset=UTF-8\"/><f:view/></jsp:root>"));
                
                // I do need CSS handling now that I'm parsing style elements
                boolean css = true;
                DocumentBuilder parser = MarkupService.createRaveSourceDocumentBuilder(css);
                target = parser.parse(is2);
            } catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
                
                return null;
            } catch (org.xml.sax.SAXException e) {
                ErrorManager.getDefault().notify(e);
                
                return null;
            } catch (javax.xml.parsers.ParserConfigurationException e) {
                ErrorManager.getDefault().notify(e);
                
                return null;
            }
            
            /*
            Node html = null;
            NodeList nl = dom.getChildNodes();
            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    html = node;
                    break;
                }
            }
            if (html == null) {
                return null;
            }
             */
            
            //Node html = dom.getDocumentElement();
            NodeList children = (NodeList) dom.getElementsByTagName(HtmlTag.HTML.name);
            
            // jsp:root serves the same role when importing JSP documents:
            // contains name space lists etc.
            if (children.getLength() < 1) {
                children = dom.getElementsByTagName("jsp:root");
            }
            
            if (children.getLength() < 1) {
                String message = NbBundle.getMessage(ImportPagePanel.class, "NoHtmlElement"); // NOI18N
                NotifyDescriptor d = new NotifyDescriptor.Message(message);
                d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                
                return null;
            }
            
            Node html = children.item(0);
            
            // Strip xmlns stuff off the html element since it causes insync to fail
            // (Can't insert default form beans etc.)
            if (html.getNodeType() == Node.ELEMENT_NODE) {
                Element he = (Element)html;
                he.removeAttribute("xmlns"); // NOI18N
                
                NamedNodeMap nnm = he.getAttributes();
                int num = nnm.getLength();
                Map map = new HashMap();
                context.nameSpaces = map;
                
                // Look for other namespace attributes to duplicate to the
                // jsp root
                for (int i = 0; i < num; i++) {
                    Node a = nnm.item(i); // XXX move element.getAttributes out of loop
                    
                    String attribute = a.getNodeName();
                    if (attribute.startsWith("xmlns:")) { // NOI18N
                        map.put(attribute, a.getNodeValue());
                    }
                }
                
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    String attribute = (String)it.next();
                    he.removeAttribute(attribute);
                }
            }
            
            // Do surgery to insert our own stuff
            children = target.getElementsByTagName("f:view"); // NOI18N
            
            if (children.getLength() < 1) {
                return null;
            }
            
            Element fview = (Element)children.item(0);
            Node copy = importNode(target, html);
            fview.appendChild(copy);
            
            // Hack
            Tidy.cleanEntities(copy, !isJsp);
            
            // Add taglibs from the top of the document
            if (dom instanceof DOMDocumentImpl) {
                //                List nodes =  ((DOMDocumentImpl) dom).getJspStartNodes();
                List<Node> nodes = ((DOMDocumentImpl)dom).getJspStartNodes();
                if( nodes != null ) {
                    for (Node node : nodes ){
                        String data = node.getNodeValue();
                        getJspxElementFromJsp(target, data);
                    }
                    //
                    //                if (nodes != null) {
                    //                    Iterator it = nodes.iterator();
                    //
                    //                    while (it.hasNext()) {
                    //                        Node node = (Node)it.next();
                    //                        String data = node.getNodeValue();
                    //                        getJspxElementFromJsp(target, data);
                    //
                    //                        // We don't use the nodes here since they are outside
                    //                        // the body, but as a side effect, the tagLibs list
                    //                        // may be modified
                    //                    }
                    //                }
                }
            }
            
            if (context.tagLibs != null) {
                int i = 0;
                Element jspRoot = target.getDocumentElement();
                
                while (i < context.tagLibs.size()) {
                    String xmlns = (String)context.tagLibs.get(i++);
                    String uri = (String)context.tagLibs.get(i++);
                    
                    if (!jspRoot.hasAttribute(xmlns)) {
                        jspRoot.setAttribute(xmlns, uri);
                    }
                }
            }
            
            context.fullUrl = resourceURL;
            context.base = computeBase(target, resourceURL);
            
            if (context.copyResources) {
                context.resources = new HashMap(50);
                int oldMode = AddResourceOverwriteDialog.getMode();
                try {
                    // Let users respond with "yes to all" or "no to all" on file conflicts
                    AddResourceOverwriteDialog.setMode(AddResourceOverwriteDialog.CONFLICT_ASK_MANY);
                    
                    copyResources(target.getDocumentElement());
                } finally {
                    AddResourceOverwriteDialog.setMode(oldMode);
                }
            }
            
            //String result = FacesSupport.getHtmlStream(target);
            //return result;
            return target;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
    }
    
    
    
    /** The actual import method. Will create the web forms etc. */
    private void finishImportSafely() {
        assert SwingUtilities.isEventDispatchThread();
        
        try {
            FileObject webformFile = context.webformFile;
            FacesModel model = getModel(webformFile);
            
            if (model == null) {
                return;
            }
            
            // Ensure that we start off in a clean state
            model.sync();
            
            UndoEvent undoEvent = null;
            
            try {
                undoEvent = model.writeLock(null);
                
                // Copy pieces from the body and from the head into the corresponding places in the document
                insertPortions(context, context.parsedDocument);
                
                /*
                if (doc instanceof BaseDocument)
                    ((BaseDocument)doc).atomicLock();
                try {
                 
                    String cleanedup = cleanup(url);
                    if (cleanedup == null) {
                        return;
                    }
                 
                    doc.remove(0, doc.getLength());
                    doc.insertString(0, cleanedup, null);
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(e);
                } finally {
                    if (doc instanceof BaseDocument)
                        ((BaseDocument)doc).atomicUnlock();
                }
                 */
                if (convert) {
                    convertCompsToJsf(context);
                }
            } finally {
                model.writeUnlock(undoEvent);
            }
            
            // Force a global element rebuild. This is necessary to ensure that
            // all the source<->render references are correct since we've been
            // doing surgery on the DOM. Without this, you might for example
            // import the Sun homepage, but you can't click on text nodes and
            // have the caret position itself inside the word until you hit Refresh.
            // This MAY be related to
            //  http://jupiter.czech.sun.com/wiki/view/Creator/InSyncAi1067
            // <move> XXX Bad API.
            //            webform.getActions().refresh(true);
            // ====
            //            DesignerServiceHack.getDefault().refresh(null, context.webformDobj, true);
            //            DesignerServiceHack.getDefault().refreshDataObject(context.webformDobj, true);
            // There is needed to refresh only the insync part, designer is not opened yet.
            model.refresh(true);
            // </move>
            
            
            OpenCookie open = (OpenCookie)context.webformDobj.getCookie(OpenCookie.class);
            
            if (open != null) {
                open.open();
            }
            
            if (context.haveOldJsp) {
                //                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
                //                        "JspConversionWarning")); // NOI18N
                IllegalStateException ex = new IllegalStateException("Old jsp"); // NOI18N
                Throwable th = ErrorManager.getDefault().annotate(ex, NbBundle.getMessage(ImportPagePanel.class, "JspConversionWarning"));
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, th);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            
            String message = NbBundle.getMessage(ImportPagePanel.class, "ImportFailed"); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }
    
    
    private static Tidy getTidy(boolean isJsp, int encoding) {
        // Set configuration settings
        Tidy tidy = new Tidy();
        tidy.setOnlyErrors(false);
        tidy.setShowWarnings(true);
        tidy.setQuiet(false);
        tidy.getConfiguration().outputJspMode = true;
        tidy.getConfiguration().inputJspMode = isJsp;
        
        //        int encoding = getEncoding();
        
        if (encoding != -1) {
            tidy.setCharEncoding(encoding);
        }
        
        // XXX Apparently JSP pages (at least those involving
        // JSF) need XML handling in order for JTidy not to choke on them
        tidy.setXmlTags(false);
        
        tidy.setXHTML(true); // XXX ?
        
        //tidy.setMakeClean(panel.getReplace());
        //tidy.setIndentContent(panel.getIndent());
        //tidy.setSmartIndent(panel.getIndent());
        //tidy.setUpperCaseTags(panel.getUpper());
        //tidy.setHideEndTags(panel.getOmit());
        //tidy.setWraplen(panel.getWrapCol());
        return tidy;
    }
    
    
    
    
    
    private static org.w3c.dom.Document rewrite(Tidy tidy, InputStream input) {
        StringBuffer sb = new StringBuffer(4000);
        OutputStream output = new StringBufferOutputStream(sb);
        
        //tidy.parse(input, output);
        StringWriter sw = new StringWriter();
        tidy.setErrout(new PrintWriter(sw));
        
        /* Show NetBeans output window with the errors? But how do I get an
         * output stream from the output writer?
                InputOutput io = IOProvider.getDefault().getStdOut();
                OutputWriter out = io.getOut();
                OutputStreamWriter
         */
        boolean escape =
                tidy.getConfiguration().outputJspMode && !tidy.getConfiguration().inputJspMode;
        org.w3c.dom.Document document =
                tidy.parseDOM(new Tidy.EntityWrapperInputStream(input),
                new Tidy.EntityWrapperOutputStream(output, escape));
        
        /* The Tidy DOM implementation sucks - it throws not implemented
           exceptions for CharacterData methods etc., so we call this on
           the duplicated node tree instead
        if (document != null) {
            tidy.cleanEntities(document, escape);
        }
         */
        
        //return sb.toString();
        if (document == null) {
            String message =
                    NbBundle.getMessage(ImportPagePanel.class, "ParsingFailed",
                    sb.toString() + "\n" + sw.getBuffer().toString()); // NOI18N
            NotifyDescriptor d = new NotifyDescriptor.Message(message);
            d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
        
        return document;
    }
    
    
    /** Import the given node into this document (but don't parent it).
     * For some freakin' reason, Xerces throws NPEs when I try to import nodes
     * from JTidy's DOM. Not sure why; perhaps its the namespace stuff that's
     * improperly implemented in JTidy's DOM implementation.
     */
    private Node importNode(Document target, Node node) {
        // Can't just use DOM's own method - xerces barfs:
        //return target.getDocumentElement().appendChild(target.importNode(node, true));
        Node curr = null;
        
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            
            Element e = (Element)node;
            Element ec = target.createElement(e.getTagName());
            curr = ec;
            
            // Copy attributes
            int num = e.getAttributes().getLength();
            
            for (int i = 0; i < num; i++) {
                Node a = e.getAttributes().item(i);
                ec.setAttribute(a.getNodeName(), a.getNodeValue());
            }
            
            break;
            
        case Node.TEXT_NODE:
            curr = target.createTextNode(node.getNodeValue());
            
            break;
            
        case Node.ENTITY_REFERENCE_NODE:
            curr = target.createEntityReference(node.getNodeValue());
            
            break;
            
        case Node.ENTITY_NODE:
            
            // We won't copy the entity into our document
            break;
            
        case Node.COMMENT_NODE:
            curr = target.createComment(node.getNodeValue());
            
            break;
            
        case Node.CDATA_SECTION_NODE:
            curr = target.createCDATASection(node.getNodeValue());
            
            break;
            
            // XXX what to do, what to do?
            //break;
        case 115: // XXX special hack. See  org.w3c.tidy.DOMNodeImpl's getNodeType()
            
            String data = node.getNodeValue();
            curr = getJspxElementFromJsp(target, data);
            
            break;
            
        case Node.ATTRIBUTE_NODE:
        case Node.PROCESSING_INSTRUCTION_NODE:
        case Node.DOCUMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.NOTATION_NODE:default:
            
            // TODO - check for other character data, ... others?
            ErrorManager.getDefault().log("Not processing node " + node);
        }
        
        NodeList nl = node.getChildNodes();
        
        for (int i = 0, n = nl.getLength(); i < n; i++) {
            Node child = importNode(target, nl.item(i));
            
            if (child != null) {
                curr.appendChild(child);
            }
        }
        
        return curr;
    }
    
    
    
    /*
     * Convert:
     * <pre>
        Regular JSP page          JSP Document
        <%@ page attribute list %>         <jsp:directive.page attribute list />
        <%@ include file="path" %>         <jsp:directive.include file="path" />
        <%! declaration %>         <jsp:declaration>declaration</jsp:declaration>
        <%= expression %>         <jsp:expression>expression</jsp:expression>
        <% scriptlet %>         <jsp:scriptlet>scriptlet</jsp:scriptlet>
     </pre>
     *
     */
    private Node getJspxElementFromJsp(Document document, String data) {
        context.haveOldJsp = true;
        
        if (data.startsWith("@")) {
            // Look for taglib
            String s = data;
            
            if (s.startsWith("@ taglib ")) {
                // taglib
                // Find prefix and uri
                int n = s.length();
                int prefix = s.indexOf(" prefix=\"");
                int uri = s.indexOf(" uri=\"");
                
                if ((prefix != -1) && (uri != -1)) {
                    StringBuffer p = new StringBuffer();
                    
                    for (int i = prefix + 9; i < n; i++) {
                        char c = s.charAt(i);
                        
                        if ((c == '"') || !Character.isLetter(c)) {
                            break;
                        }
                        
                        p.append(c);
                    }
                    
                    StringBuffer u = new StringBuffer();
                    
                    for (int i = uri + 6; i < n; i++) {
                        if (s.charAt(i) == '"') {
                            break;
                        }
                        
                        u.append(s.charAt(i));
                    }
                    
                    if ((p.length() > 0) && (u.length() > 0)) {
                        String xmlns = "xmlns:" + p.toString(); // NOI18N
                        
                        if (context.tagLibs == null) {
                            context.tagLibs = new ArrayList();
                        }
                        
                        context.tagLibs.add(xmlns);
                        context.tagLibs.add(u.toString());
                        
                        /*
                        if (!jspRoot.hasAttribute(xmlns)) {
                            jspRoot.setAttribute(xmlns, u.toString());
                        }
                         */
                    }
                }
                
                // TODO: }  else if (s.startsWith("@ include ")) {
            } else if (s.startsWith("@ page ")) {
                // Find the attribute list
                // XXX. this is wrong, I should change this to do arbitrary attribute list conversion
                int n = s.length();
                int imp = s.indexOf(" import=\"");
                
                if (imp != -1) {
                    StringBuffer p = new StringBuffer();
                    
                    for (int i = imp + 9; i < n; i++) {
                        char c = s.charAt(i);
                        
                        if ((c == '"') || !Character.isLetter(c)) {
                            break;
                        }
                        
                        p.append(c);
                    }
                    
                    if (p.length() > 0) {
                        Element e = document.createElement("jsp:directive.page");
                        e.setAttribute("import", p.toString());
                        
                        return e;
                    }
                }
            }
        } else if (data.startsWith("!")) {
            Element e = document.createElement("jsp:declaration");
            e.appendChild(document.createTextNode(data.substring(1))); // skip !
            
            return e;
        } else if (data.startsWith("=")) {
            Element e = document.createElement("jsp:expression");
            e.appendChild(document.createTextNode(data.substring(1))); // skip !
            
            return e;
        } else {
            // Just a scriptlet
            Element e = document.createElement("jsp:scriptlet");
            e.appendChild(document.createTextNode(data));
            
            return e;
        }
        
        return null;
    }
    
    
    
    /**
     * Returns the location to resolve relative URLs against.  By
     * default this will be the document's URL if the document
     * was loaded from a URL.  If a base tag is found and
     * can be parsed, it will be used as the base location.
     *
     * @return the base location
     */
    public URL computeBase(Document document, URL fullUrl) {
        // First see if we have a <base> tag within the <head>
        URL base = null;
        
        // TODO - gather ALL <base> elements within the head
        // and process them
        Element root = document.getDocumentElement();
        Element html = findHtmlTag(root);
        
        if (html != null) {
            Element head = findElement(HtmlTag.HEAD.name, html);
            
            if (head != null) {
                Element baseElement = findElement(HtmlTag.BASE.name, head);
                
                if (baseElement != null) {
                    String href = baseElement.getAttribute(HtmlAttribute.HREF);
                    
                    if ((href != null) && (href.length() > 0)) {
                        try {
                            base = new URL(href);
                            
                            return base;
                        } catch (MalformedURLException ex) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                        }
                    }
                }
            }
        }
        
        // No <base>, so use the URL of the document file itself
        // and use that to resolve relative URLs.
        // However, we can't simply strip off the basename and use that
        // as the base, because of "ambiguous" urls, such as
        // 'http://wwws.sun.com/software/products/jscreator'. The REAL
        // URL to the resource is
        // 'http://wwws.sun.com/software/products/jscreator/index.html'
        // but we don't know that -- the HTTP server on the receiving end
        // will check if the pointed to URL is really a directory, and if
        // so pick a default file (typically index.html) within it.
        // Unfortunately it's hard for me to detect if this is the case.
        // I thought I could just try to add "/index.html" to the given
        // URL and see if that "exists", but that won't work either --
        // the web server will redirect this request to some other page
        // (perhaps an error page) and serve that back, and I can't tell
        // that content from regular content. So instead the caller will
        // simply have to use the fullURL as a "backup" url to try
        // when copying a resource fails.
        try {
            String file = new File(fullUrl.getFile()).getParent() + "/";
            base = new URL(fullUrl.getProtocol(), fullUrl.getHost(), fullUrl.getPort(), file);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return base;
    }
    
    
    
    /** Copy the resources from the given URL to the given project */
    private void copyResources(Node node) {
        // TODO: iterate over the DOM, looking for resources I should copy,
        // and rewrite the URLs if necessary and copy the resource
        if (!(node instanceof Element)) {
            return;
        }
        
        Element element = (Element)node;
        String tag = element.getTagName();
        
        // What about jsp include??
        if (HtmlTag.IMG.name.equals(tag)) {
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.SCRIPT.name.equals(tag) &&
                (element.getAttribute(HtmlAttribute.SRC).length() > 0)) {
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.STYLE.name.equals(tag)) {
            handleStyleSheet(0, element, null);
        } else if (HtmlTag.LINK.name.equals(tag) &&
                "stylesheet".equalsIgnoreCase(element.getAttribute(HtmlAttribute.REL))) { // NOI18N
            handleStyleSheet(0, element, HtmlAttribute.HREF);
        } else if (HtmlTag.INPUT.name.equals(tag) &&
                "image".equals(element.getAttribute(HtmlAttribute.TYPE))) { // NOI18N
            copyResource(element, HtmlAttribute.SRC);
        } else if (HtmlTag.OBJECT.name.equals(tag) &&
                (element.getAttribute(HtmlAttribute.SRC).length() > 0)) {
            copyResource(element, HtmlAttribute.SRC);
        }
        
        if (element.getAttribute(HtmlAttribute.STYLE).length() > 0) {
            handleStyleSheet(0, element, HtmlAttribute.STYLE);
        }
        
        // XXX what about <frame> and <iframe> -- should I copy these too?
        // More likely I should recursively import them, not just copy, such
        // that their images are also copied. I should use the same name map
        // to make sure I don't duplicate copies of shared resources, and
        // I can use this to make sure that I don't get in infinite recursion
        // as well by putting my own url in there so that other pages referring
        // to me simply use the new url value returned by the resources map!
        NodeList nl = element.getChildNodes();
        
        for (int i = 0, n = nl.getLength(); i < n; i++) {
            System.out.println("Item i: " + i + " is " + nl.item(1));
            copyResources(nl.item(i));
        }
    }
    
    /** Rewrite the given url attribute for the given element to
     * point to a local copy of the resource, and create that local
     * copy. Take the URL from the given element and attribute,
     * and store the new project relative uri in there when done. */
    private void copyResource(Element element, String urlAttribute) {
        // Look up the url
        String urlString = element.getAttribute(urlAttribute);
        
        if (urlString.length() == 0) {
            return;
        }
        
        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            // Rewrite html url
            element.setAttribute(urlAttribute, (String)context.resources.get(urlString));
            
            return;
        }
        
        String projectUrl = copyResource(urlString);
        
        if (projectUrl != null) {
            element.setAttribute(urlAttribute, projectUrl);
        }
    }
    
    /** Rewrite the given url attribute for the given element to
     * point to a local copy of the resource, and create that local
     * copy. */
    private String copyResource(String urlString) {
        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            return (String)context.resources.get(urlString);
        }
        
        URL url;
        
        try {
            url = new URL(context.base, urlString);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
            
            return null;
        }
        
        // Copy resource into project
        try {
            String projectPath = null;
            
            // XXX Shouldn't JsfProjectUtils take a project parameter?
            projectPath = JsfProjectUtils.addResource(context.webformFile, url, true);
            
            if (projectPath == null) {
                // XXX what do we do?  The user has cancelled when
                // there was a conflict warning in the PM - but the
                // PM returned null so we don't know exactly what the
                // conflict path was -- I'd like to use it here
                // Does this also happen if we pass in a bogus url (e.g. a src to an image
                // where the image doesn't actually exist?)
                return null;
            }
            
            String projectUrl = MarkupUnit.toURL(projectPath);
            context.resources.put(urlString, projectUrl);
            
            return projectUrl;
        } catch (java.io.FileNotFoundException fnfe) {
            // Try with full url as base instead; see comment in computeBase() for why
            // this is necessary
            URL fullUrl = context.fullUrl;
            
            try {
                //url = new URL(fullUrl, urlString);
                url = new URL(fullUrl.getProtocol(), fullUrl.getHost(), fullUrl.getPort(),
                        fullUrl.getFile() + "/" + urlString);
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
                
                return null;
            }
            
            // Copy resource into project
            try {
                String projectPath = null;
                
                // XXX Shouldn't JsfProjectUtils take a project parameter?
                projectPath = JsfProjectUtils.addResource(context.webformFile, url, true);
                
                if (projectPath == null) {
                    // XXX what do we do?  The user has cancelled when
                    // there was a conflict warning in the PM - but the
                    // PM returned null so we don't know exactly what the
                    // conflict path was -- I'd like to use it here
                    return null;
                }
                
                String projectUrl = MarkupUnit.toURL(projectPath);
                
                // Rewrite html url
                context.resources.put(urlString, projectUrl);
                
                return projectUrl;
            } catch (java.net.UnknownHostException uhe) {
                // Open the output window instead and tell the user that the resource couldn't
                // be imported
                if (context.warnMissingFile) {
                    //                    InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
                    //                            "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString)); // NOI18N
                    IllegalStateException ise = new IllegalStateException(uhe);
                    Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString));
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, uhe);
                }
            } catch (java.io.FileNotFoundException fnfe2) {
                if (context.warnMissingFile) {
                    //                    InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
                    //                            "NoSuchResource", urlString)); // NOI18N
                    IllegalStateException ise = new IllegalStateException(fnfe2);
                    Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", urlString));
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
                } else {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, fnfe2);
                }
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } catch (java.net.UnknownHostException uhe) {
            // Open the output window instead and tell the user that the resource couldn't
            // be imported
            if (context.warnMissingFile) {
                //                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
                //                        "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString)); // NOI18N
                IllegalStateException ise = new IllegalStateException(uhe);
                Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", uhe.getLocalizedMessage() + ": " + urlString));
                ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
            } else {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, uhe);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        
        return null;
    }
    // <move-first>
    private static FacesModel getModel(FileObject fo) {
        return FacesModel.getInstance(fo);
    }
    
    
    private void insertPortions(ImportContext context, Document document) {
        FileObject webformFile = context.webformFile;
        LiveUnit unit = getModel(webformFile).getLiveUnit();
        
        // Locate <body> in document. Look for <ui:body> too?
        NodeList importBodys = document.getElementsByTagName(HtmlTag.BODY.name);
        
        if (importBodys.getLength() == 0) {
            importBodys = document.getElementsByTagName("ui:body"); // NOI18N
        }
        
        if (importBodys.getLength() > 0) {
            Node importbody = importBodys.item(0);
            MarkupDesignBean body;
            if (context.fragment) {
                MarkupBean bean = getModel(webformFile).getFacesUnit().getDefaultParent();
                //                RaveElement element = (RaveElement)bean.getElement();
                //                body = element.getDesignBean();
                Element element = bean.getElement();
                body = InSyncService.getProvider().getMarkupDesignBeanForElement(element);
                for (int i = 0; i < body.getChildBeanCount(); i++) {
                    DesignBean child = body.getChildBean(i);
                    if (child.getInstance() instanceof org.netbeans.modules.visualweb.xhtml.P) {
                        unit.deleteBean(child);
                        break;
                    }
                }
            } else {
                DesignBean[] bodys;
                if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                    // XXX Woodstock
                    bodys = unit.getBeansOfType(com.sun.webui.jsf.component.Body.class);
                } else {
                    // XXX Braveheart
                    bodys = unit.getBeansOfType(com.sun.rave.web.ui.component.Body.class);
                }
                
                if ((bodys == null) || (bodys.length == 0)) {
                    bodys = unit.getBeansOfType(org.netbeans.modules.visualweb.xhtml.Body.class);
                    
                    if ((bodys == null) || (bodys.length == 0)) {
                        // XXX ERROR
                        ErrorManager.getDefault().log("No body found");
                        
                        return;
                    }
                }
                
                body = (MarkupDesignBean)bodys[0];
            }
            
            //Duplicate attributes on the body... when applicable
            //        like bgcolor etc.
            //        TODO: bgcolor !
            Element importBodyElement = (Element)importbody;
            String style = importBodyElement.getAttribute(HtmlAttribute.STYLE);
            String bgcolor = importBodyElement.getAttribute(HtmlAttribute.BGCOLOR);
            if (bgcolor.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";background-color:"+bgcolor; // NONI18N
            }
            String background = importBodyElement.getAttribute(HtmlAttribute.BACKGROUND);
            if (background.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";background-image:url("+background+")"; // NOI18N
            }
            String text = importBodyElement.getAttribute(HtmlAttribute.TEXT);
            if (text.length() > 0) {
                if (style.length() == 0) {
                    style = ";"; // NOI18N
                }
                style = style+";color"+text; // NOI18N
            }
            
            DesignProperty styleProp = null;
            if (context.fragment && body.getBeanParent() != null) {
                styleProp = body.getBeanParent().getProperty("style");
            } else {
                styleProp = body.getProperty("style");
            }
            if (styleProp != null) {
                styleProp.setValue(style);
            }
            
            String onload = importBodyElement.getAttribute(HtmlAttribute.ONLOAD);
            if (onload.length() > 0) {
                DesignProperty onLoadProp = body.getProperty("onLoad");
                if (onLoadProp != null) {
                    onLoadProp.setValue(onload);
                }
            }
            
            //            String onunload = importBodyElement.getAttribute(HtmlAttribute.ONUNLOAD);
            //            if (onunload.length() > 0) {
            //                body.getProperty("onUnload").setValue(onunload);
            //
            //            }
            
            String cls = importBodyElement.getAttribute(HtmlAttribute.CLASS);
            if (cls.length() > 0) {
                DesignProperty styleClassProp = body.getProperty("styleClass");
                if (styleClassProp != null) {
                    styleClassProp.setValue(cls); // NOI18N
                }
            }
            
            NodeList nl = importbody.getChildNodes();
            Element parent = body.getElement();
            
            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node copied = getDom(webformFile).importNode(nl.item(i), true);
                parent.appendChild(copied);
            }
            
            // Eliminate duplicate <title> ?
            // Eliminate duplicate <meta> ?
        }
        
        // Locate <head> in document. Look for <ui:head> too?
        NodeList importHeads = document.getElementsByTagName(HtmlTag.HEAD.name);
        
        if (importHeads.getLength() == 0) {
            importHeads = document.getElementsByTagName("ui:head"); // NOI18N
        }
        
        if (importHeads.getLength() > 0) {
            Node importHead = importHeads.item(0);
            DesignBean[] heads;
            if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                // Woodstock
                heads = unit.getBeansOfType(com.sun.webui.jsf.component.Head.class);
            } else {
                // XXX Braveheart
                heads = unit.getBeansOfType(com.sun.rave.web.ui.component.Head.class);
            }
            
            if ((heads == null) || (heads.length == 0)) {
                heads = unit.getBeansOfType(org.netbeans.modules.visualweb.xhtml.Head.class);
                
                if ((heads == null) || (heads.length == 0)) {
                    // XXX ERROR
                    ErrorManager.getDefault().log("No head found");
                    
                    return;
                }
            }
            
            MarkupDesignBean head = (MarkupDesignBean)heads[0];
            NodeList nl = importHead.getChildNodes();
            Element parent = head.getElement();
            
            for (int i = 0, n = nl.getLength(); i < n; i++) {
                Node copied = getDom(webformFile).importNode(nl.item(i), true);
                parent.appendChild(copied);
            }
            
            // Eliminate duplicate <title> ?
            // Eliminate duplicate <meta> ?
        }
        
        // Duplicate name spaces
        if (context.nameSpaces != null) {
            Element root = getDom(webformFile).getDocumentElement();
            Iterator it = context.nameSpaces.keySet().iterator();
            while (it.hasNext()) {
                String attribute = (String)it.next();
                if (!root.hasAttribute(attribute)) {
                    root.setAttribute(attribute,  (String)context.nameSpaces.get(attribute));
                }
            }
        }
    }
    
    
    
    
    /** Convert the input components to JSF components */
    private void convertCompsToJsf(ImportContext context) {
        FileObject webformFile = context.webformFile;
        FacesModel model = getModel(webformFile);
        
        if (model == null) {
            return;
        }
        
        LiveUnit lunit = model.getLiveUnit();
        
        if (lunit == null) {
            return;
        }
        
        // Iterate over the DOM, change <input>, <select> etc. tags
        //MarkupUnit munit = webform.getMarkup();
        Document document = getDom(webformFile);
        
        convertCompsToJsf(document.getDocumentElement(), webformFile, importImages,
                convertForms);
        
        // Clean up forms
        if (context.formsAdded != null) {
            removeBogusForms(context.formsAdded, webformFile);
        }
        
        // Get rid of old element references etc.
        //        model.getMarkupUnit().setSourceDirty(); // we've been messing with the DOM
        //        model.sync();
        model.getMarkupUnit().setModelDirty();
        model.getJavaUnit().setModelDirty();
        model.flush();
        LifecycleManager.getDefault().saveAll();
    }
    
    
    private void convertCompsToJsf(Element element, FileObject webformFile, boolean images,
            boolean formComps) {
        String tagName = element.getTagName();
        HtmlTag tag = HtmlTag.getTag(tagName);
        DesignBean bean = null;
        
        // Declare early so specific components can prune their children
        // by nulling out
        boolean skipChildren = false;
        
        if (tag != null) {
            char c = tagName.charAt(0);
            
            switch (c) {
            case 'l':
                
                if (tag == HtmlTag.LABEL) {
                    bean =
                            replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlOutputLabel.class.getName()); // NOI18N
                    
                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.FOR, "for"); // NOI18N
                    }
                }
                
                break;
                
            case 'f':
                
                if (formComps && (tag == HtmlTag.FORM)) {
                    //bean = replaceComponent(webform, element, "javax.faces.component.html.HtmlForm"); // NOI18N
                    if (JsfProjectUtils.JAVA_EE_5.equals(JsfProjectUtils.getJ2eePlatformVersion(context.project))) {
                        // Woodstock
                        bean = replaceComponent(webformFile, element, com.sun.webui.jsf.component.Form.class.getName());
                    } else {
                        // Braveheart
                        bean = replaceComponent(webformFile, element, com.sun.rave.web.ui.component.Form.class.getName());
                    }
                    
                    if (bean != null) {
                        if (context.formsAdded == null) {
                            context.formsAdded = new ArrayList();
                        }
                        
                        context.formsAdded.add(bean);
                        // TODO - check these
                        //convertProperty(bean, element, HtmlAttribute.TARGET, "target"); // NOI18N
                        //convertProperty(bean, element, "accept", "accept"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.ENCTYPE, "enctype"); // NOI18N
                        convertProperty(bean, element, "onsubmit", "onSubmit"); // NOI18N
                        convertProperty(bean, element, "onreset", "onReset"); // NOI18N
                        //convertProperty(bean, element, "accept-charset", "acceptcharset"); // NOI18N
                    }
                }
                
                break;
                
            /*
            case 'a':
            if (tag == HtmlTag.A) {
                bean = replaceComponent(webform, element, "javax.faces.component.html.HtmlOutputLink"); // NOI18N
                if (bean != null) {
                    convertProperty(bean, element, HtmlAttribute.HREF, "value"); // NOI18N
                }
             
             
                // Also, HtmlOutputLink does not render its markup children,
                // only its component children, so I've gotta insert an
                // output text to contain its link text!
            }
            break;
             */
            case 'i':
                
                if (formComps && (tag == HtmlTag.INPUT)) {
                    String type = element.getAttribute(HtmlAttribute.TYPE);
                    
                    if ((type == null) || (type.length() == 0) || type.equals("text")) { // NOI18N
                        
                        // Text Field
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputText.class.getName());
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.MAXLENGTH, "maxlength"); // NOI18N
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                            convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                        }
                    } else if (type.equals("submit") // NOI18N
                            ||type.equals("reset") // NOI18N
                            ||type.equals("button")) { // NOI18N
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlCommandButton.class.getName());
                    } else if (type.equals("checkbox")) {
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectBooleanCheckbox.class.getName());
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.CHECKED, "value"); // NOI18N
                        }
                    } else if (type.equals("file")) { // NOI18N
                        
                        // XXX no file upload component!!
                    } else if (type.equals("hidden")) { // NOI18N
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputHidden.class.getName());
                    } else if (type.equals("password")) { // NOI18N
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlInputSecret.class.getName());
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.MAXLENGTH, "maxlength"); // NOI18N
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                            convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                        }
                    } else if (type.equals("radio")) {
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectManyCheckbox.class.getName());
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.CHECKED, "value"); // NOI18N
                        }
                    } else if ("image".equals(type)) {
                        // HtmlCommandButton with image attribute
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlCommandButton.class.getName());
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.SRC, "image"); // NOI18N
                        }
                    }
                    
                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N
                        
                        // Not a JSF prop: convertProperty(bean, element, "accept", "accept"); // NOI18N
                    }
                }
                // CONTROVERSIAL:
                else if (images && (tag == HtmlTag.IMG)) {
                    bean =
                            replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlGraphicImage.class.getName());
                    
                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.SRC, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.WIDTH, "width"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.HEIGHT, "height"); // NOI18N
                        convertProperty(bean, element, "longdesc", "longdesc"); // NOI18N
                        
                        // TODO: usemap, ismap
                    }
                }
                
                break;
                
                // CONTROVERSIAL:
            case 's':
                
                if (formComps && (tag == HtmlTag.SELECT)) {
                    int size =
                            HtmlAttribute.getIntegerAttributeValue(element, HtmlAttribute.SIZE, 1);
                    boolean multiple = element.hasAttribute(HtmlAttribute.MULTIPLE);
                    
                    if ((size > 1) || multiple) {
                        // javax.faces.component.html.HtmlSelectOneListbox
                        // javax.faces.component.html.HtmlSelectManyListbox
                        // XXX Todo: look up selection attribute and do the right thing!
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectOneListbox.class.getName(), false);
                        
                        if (bean != null) {
                            convertProperty(bean, element, HtmlAttribute.SIZE, "size"); // NOI18N
                        }
                    } else {
                        bean =
                                replaceComponent(webformFile, element,
                                javax.faces.component.html.HtmlSelectOneMenu.class.getName(), false);
                    }
                    
                    if (bean != null) {
                        skipChildren = true; // Option children should not be included!
                        
                        // XXX will this conflict with checkbox/radiobox
                        // setting value from the "checked" attribute?
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.CLASS, "styleClass"); // NOI18N
                        
                        // Create a select items!
                        // XXX I've gotta get the id set first... so
                        // that I pick a decent select items name!
                        String[] optionList = extractOptions(element);
                        
                        try {
                            DesignContext context = bean.getDesignContext();
                            
                            // create and setup a default items array
                            DesignBean items =
                                    context.createBean(DefaultSelectItemsArray.class.getName(), null,
                                    null);
                            items.setInstanceName(bean.getInstanceName() + "DefaultItems", true); //NOI18N
                            
                            DesignProperty itp = items.getProperty("items"); // NOI18N
                            
                            if (itp != null) {
                                itp.setValue(optionList);
                            }
                            
                            // create a selectitems child
                            if (context.canCreateBean(UISelectItems.class.getName(), bean, null)) {
                                DesignBean si =
                                        context.createBean(UISelectItems.class.getName(), bean, null);
                                
                                if (si != null) {
                                    si.setInstanceName(bean.getInstanceName() + "SelectItems", true); //NOI18N
                                    
                                    String outer =
                                            bean.getDesignContext().getRootContainer().getInstanceName();
                                    si.getProperty("value").setValueSource("#{" + outer + "." + //NOI18N
                                            items.getInstanceName() + "}"); //NOI18N
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Todo: nuke out all children; possibly convert the
                    // <option> tags to a SelectItems object and populate it,
                    // then nuke the <option> tags!
                    
                    /*
                    } else if (tag == HtmlTag.SPAN) {
                    // CONTROVERSIAL!
                    // javax.faces.component.html.HtmlOutputText
                    // javax.faces.component.html.HtmlPanelGroup
                     */
                }
                
                break;
                
            case 't':
                
                if (formComps && (tag == HtmlTag.TEXTAREA)) {
                    bean =
                            replaceComponent(webformFile, element,
                            javax.faces.component.html.HtmlInputTextarea.class.getName());
                    
                    if (bean != null) {
                        convertProperty(bean, element, HtmlAttribute.VALUE, "value"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.ROWS, "rows"); // NOI18N
                        convertProperty(bean, element, HtmlAttribute.COLS, "cols"); // NOI18N
                        convertProperty(bean, element, "readonly", "readonly"); // NOI18N
                    }
                    
                    /*
                    } else if (tag == HtmlTag.TABLE) {
                    // CONTROVERSIAL:
                    // javax.faces.component.html.HtmlDataTable
                    // javax.faces.component.html.HtmlPanelGrid
                     */
                }
                
                break;
                
            case 'o':
                
                //                if (tag == HtmlTag.OPTION) {
                //                    // Should convert these as part of <select>'s children.
                //                    // When they appear outside of a select, wipe them out.
                //                    // TODO
                //                }
                
                break;
            }
            
            // XXX How do we convert to checkbox lists and radiobutton lists?
        } else {
            // Use reverse map from tags to class names to instantiate
            // a component behind the tag
            initPaletteComponents();
            
            int colon = tagName.indexOf(':');
            
            if (colon != -1) {
                tagName = tagName.substring(colon + 1);
            }
            
            String cls = (String)context.paletteComponents.get(tagName);
            
            if (cls != null) {
                // I may already have some existing DesignBeans in the document
                // I'm mutating -- the default parent form for example; don't
                // do anything with these.
                //                if (((RaveElement)element).getDesignBean() == null) {
                if (InSyncService.getProvider().getMarkupDesignBeanForElement(element) == null) {
                    // we don't set "bean" -- don't want html treatment below
                    DesignBean newBean = replaceTag(webformFile, element, cls);
                    
                    // If I've replaced the Element, ensure that I recurse into
                    // the new element instead
                    if (newBean != null) {
                        if (tagName.equals(HtmlTag.FORM.name)) {
                            if (context.formsAdded == null) {
                                context.formsAdded = new ArrayList();
                            }
                            
                            context.formsAdded.add(newBean);
                        }
                        
                        // <move>
                        //                        Element e = FacesSupport.getElement(newBean);
                        // ====
                        Element e = getElement(newBean);
                        // </move>
                        
                        if (e != null) {
                            element = e;
                            
                            // See if we should import some additional resources
                            // I don't actually know which component properties represent URLs
                            // Is there metadata for this?
                            // Should I try to simply iterate over all properties, looking to
                            // see if they are importable resources?? Seems like this could
                            // have some accidental effects
                            String propName = null;
                            
                            if (newBean.getInstance() instanceof com.sun.rave.web.ui.component.ImageComponent
                                    || newBean.getInstance() instanceof com.sun.webui.jsf.component.ImageComponent) { // XXX Woodstock
                                propName = "url"; // NOI18N
                            } else if (newBean.getInstance() instanceof UIGraphic) {
                                propName = "value"; // NOI18N
                            } else if (newBean.getInstance() instanceof HtmlCommandButton) {
                                propName = "image"; // NOI18N
                            } // XXX are there any known other ones?
                            
                            if (context.copyResources && (propName != null)) {
                                DesignProperty prop = newBean.getProperty(propName);
                                
                                if (prop != null) {
                                    String value = prop.getValueSource();
                                    
                                    if ((value != null) && (value.length() > 0) &&
                                            // <move>
                                            //                                            !FacesSupport.isValueBindingExpression(value, false)) {
                                            // ====
                                            !isValueBindingExpression(value, false)) {
                                        // </move>
                                        context.warnMissingFile = false;
                                        copyResource(element, propName);
                                        
                                        String copied = element.getAttribute(propName);
                                        
                                        if ((copied != value) && (copied != null) &&
                                                (copied.length() > 0)) {
                                            prop.setValue(copied);
                                        }
                                        
                                        context.warnMissingFile = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (bean != null) {
            // XXX Can I preserve the name somehow?
            String id = null;
            
            if (element.getAttribute(HtmlAttribute.ID).length() > 0) {
                id = element.getAttribute(HtmlAttribute.ID);
            } else if (element.getAttribute(HtmlAttribute.NAME).length() > 0) {
                id = element.getAttribute(HtmlAttribute.NAME);
            }
            
            if (id != null) {
                // Make sure id is unique
                if (context.names == null) {
                    context.names = new HashSet();
                    context.names.add(id);
                } else {
                    while (context.names.contains(id)) {
                        id = id + "_";
                    }
                    
                    context.names.add(id);
                }
                
                bean.setInstanceName(id);
            }
            
            convertProperty(bean, element, HtmlAttribute.CLASS, "styleClass"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.STYLE, "style"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.ALT, "alt"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.TITLE, "title"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.DIR, "dir"); // NOI18N
            convertProperty(bean, element, HtmlAttribute.LANG, "lang"); // NOI18N
            convertProperty(bean, element, "disabled", "disabled"); // NOI18N
            convertProperty(bean, element, "accesskey", "accesskey"); // NOI18N
            convertProperty(bean, element, "tabindex", "tabindex"); // NOI18N
            
            // Copy all the JavaScript properties... "onblur", "onchange", ...
            convertProperty(bean, element, "onblur", "onblur"); // NOI18N
            convertProperty(bean, element, "onchange", "onchange"); // NOI18N
            convertProperty(bean, element, "onclick", "onclick"); // NOI18N
            convertProperty(bean, element, "ondblclick", "ondblclick"); // NOI18N
            convertProperty(bean, element, "onfocus", "onfocus"); // NOI18N
            convertProperty(bean, element, "okeydownn", "oneydownn"); // NOI18N
            convertProperty(bean, element, "onkeypress", "onkeypress"); // NOI18N
            convertProperty(bean, element, "onkeyup", "onkeyup"); // NOI18N
            convertProperty(bean, element, "onmousedown", "onmousedown"); // NOI18N
            convertProperty(bean, element, "onmousemove", "onmousemove"); // NOI18N
            convertProperty(bean, element, "onmouseout", "onmouseout"); // NOI18N
            convertProperty(bean, element, "onmouseover", "onmouseover"); // NOI18N
            convertProperty(bean, element, "onmouseup", "onmouseup"); // NOI18N
            convertProperty(bean, element, "onselect", "onselect"); // NOI18N
            
            // If I've replaced the Element, ensure that I recurse into
            // the new element instead
            // <move>
            //            Element e = FacesSupport.getElement(bean);
            // ====
            Element e = getElement(bean);
            // </move>
            
            if (e != null) {
                element = e;
            }
        }
        
        if (skipChildren) {
            return;
        }
        
        NodeList nl = element.getChildNodes();
        
        if ((nl == null) || (nl.getLength() == 0)) {
            return;
        }
        
        // Copy children list first since we're mutating while we fly
        ArrayList children = new ArrayList(nl.getLength());
        
        for (int i = 0, n = nl.getLength(); i < n; i++) {
            Node child = nl.item(i);
            
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
        }
        
        for (int i = 0, n = children.size(); i < n; i++) {
            Element e = (Element)children.get(i);
            convertCompsToJsf(e, webformFile, images, formComps);
        }
    }
    
    
    
    /** Locate the &lt;html&gt; tag. In a normal xhtml/html document, it's
     * the same as the root tag for the DOM, but in our JSF files, it
     * might be nested within &lt;jsp:root&gt;, &lt;f:view&gt;, etc.
     * @param parent The root tag
     * @todo Just pass in the Document node instead?
     * @todo Move to Utilities?
     * @return The html tag Element
     */
    private Element findHtmlTag(Node root) {
        if (root.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)root;
            
            if (HtmlTag.HTML.name.equals(element.getTagName())) { // Don't allow "HTML"
                
                return element;
            }
        }
        
        NodeList list = root.getChildNodes();
        int len = list.getLength();
        
        for (int i = 0; i < len; i++) {
            Node child = list.item(i);
            Element match = findHtmlTag(child);
            
            if (match != null) {
                return match;
            }
        }
        
        return null;
    }
    
    /** Locate an element of the given tag name as a direct child of
     * the given parent. If no element of that tag name is found it
     * will either return null if the create flag is false, or if the
     * create flag is true, the element will be created and inserted
     * before it is returned.
     * @todo Move to Utilities?
     * @param tag The tag name of the tag to be found or created
     * @param parent The element parent under which we want to search
     * @param create If true, create the element if it doesn't exist,
     *    otherwise return null if the tag is not found.
     */
    private Element findElement(String tag, Node parent) {
        NodeList list = parent.getChildNodes();
        int len = list.getLength();
        
        for (int i = 0; i < len; i++) {
            org.w3c.dom.Node child = list.item(i);
            
            if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element)child;
                
                if (tag.equals(element.getTagName())) {
                    return element;
                }
            }
        }
        
        return null;
    }
    
    
    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private void handleStyleSheet(int depth, Element element, String urlAttribute) {
        if (HtmlAttribute.STYLE.equals(urlAttribute)) {
            String rules = element.getAttribute(HtmlAttribute.STYLE);
            
            if (rules.length() == 0) {
                return;
            }
            
            ////            Document doc = MarkupUnit.createEmptyDocument(true);
            ////
            ////            // XXX I should share the engine for all the stylesheets
            //////            doc.setUrl(context.base);
            ////            InSyncService.getProvider().setUrl(doc, context.base);
            ////
            ////            // <markup_separation>
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
            ////            // ====
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
            //////            // </markup_separation>
            //////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
            //////            if (doc != null) {
            ////////                doc.setCssEngine(ces);
            //////                CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
            //////            }
            ////            CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
            //////            XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
            ////// </moved from engine impl>
            ////
            //////            StyleDeclaration sd = ces.parseStyleDeclaration((RaveElement)element, rules);
            ////            StyleDeclaration sd = CssProvider.getEngineService().parseStyleDeclarationForElement(element, rules);
            ////
            ////            Map rewrite = importStyleResources(depth, sd);
            Map rewrite = getStyleResourcesForElement(element, rules);
            
            if (rewrite.size() > 0) {
                String newStylesheet = replaceStrings(rewrite, rules);
                element.setAttribute(HtmlAttribute.STYLE, newStylesheet);
            }
            
            return;
        } else if (element.getTagName().equals(HtmlTag.STYLE.name)) {
            String rules = MarkupService.getStyleText(element);
            
            if (rules.length() == 0) {
                return;
            }
            
            ////            Document doc = MarkupUnit.createEmptyDocument(true);
            ////
            ////            // XXX I should share the engine for all the stylesheets
            //////            doc.setUrl(context.base);
            ////            InSyncService.getProvider().setUrl(doc, context.base);
            ////
            ////            // <markup_separation>
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
            ////            // ====
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
            //////            XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
            //////            // </markup_separation>
            //////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
            //////            if (doc != null) {
            ////////                doc.setCssEngine(ces);
            //////                CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
            //////            }
            ////            CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
            //////            XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
            ////// </moved from engine impl>
            ////            InputSource is = new InputSource(new StringReader(rules));
            //////            StyleSheet ss = ces.parseStyleSheet(is, context.base, "all", context.base);
            ////            StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, context.base, "all", context.base); // NOI18N
            ////
            ////            Map rewrite = importStyleResources(depth, doc, ss);
            Map rewrite = getStyleResources(rules, depth);
            
            if (rewrite.size() > 0) {
                String newStylesheet = replaceStrings(rewrite, rules);
                
                // Replace child nodes of the style element
                while (element.getChildNodes().getLength() > 0) {
                    element.removeChild(element.getFirstChild());
                }
                
                element.appendChild(element.getOwnerDocument().createComment(newStylesheet));
            }
            
            return;
        }
        
        String urlString = element.getAttribute(urlAttribute);
        
        if (urlString.length() == 0) {
            return;
        }
        
        // Reuse existing resource if we've already copied it in during this import
        if (context.resources.get(urlString) != null) {
            // Rewrite html url
            element.setAttribute(urlAttribute, (String)context.resources.get(urlString));
            
            return;
        }
        
        String relPath = handleStyleSheet(depth, urlString);
        
        if (relPath != null) {
            element.setAttribute(urlAttribute, relPath);
        }
    }
    
    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private String handleStyleSheet(int depth, String urlString) {
        URL url;
        
        try {
            url = new URL(context.base, urlString);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
            
            return null;
        }
        
        return handleStyleSheet(depth, urlString, url);
    }
    
    /** Import the given stylesheet into the project, rewrite the url,
     * and recursively copy other stylesheets and images referred to
     * by the stylesheet. The stylesheet references should also be
     * changed to reflect the new names. */
    private String handleStyleSheet(int depth, String urlString, URL url) {
        if (depth > 15) {
            // Probably circular references!
            // XXX Probably???
            return null;
        }
        
        ////        // XXX do I have to do the context.fullUrl trick here too, as in copyResource????
        ////        // Parse the stylesheet
        ////        Document doc = MarkupUnit.createEmptyDocument(true);
        //////        doc.setUrl(context.base);
        ////        InSyncService.getProvider().setUrl(doc, context.base);
        ////
        ////        // XXX I should share the engine for all the stylesheets
        ////        // <markup_separation>
        //////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, null, doc.getUrl());
        ////        // ====
        //////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, doc.getUrl());
        //////        XhtmlCssEngine ces = XhtmlCssEngine.create(doc, InSyncService.getProvider().getUrl(doc));
        //////        // </markup_separation>
        //////// <moved from engine impl> it doesn't (shoudn't) know about RaveDocument.
        //////        if (doc != null) {
        ////////            doc.setCssEngine(ces);
        //////            CssEngineServiceProvider.getDefault().setCssEngine(doc, ces);
        //////        }
        ////        CssProvider.getEngineService().createCssEngineForDocument(doc, InSyncService.getProvider().getUrl(doc));
        //////        XhtmlCssEngine ces = CssEngineServiceProvider.getDefault().getCssEngine(doc);
        ////// </moved from engine impl>
        ////        InputSource is = new InputSource(url.toString());
        //////        StyleSheet ss = ces.parseStyleSheet(is, url, "all", url);
        ////        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, url, "all", url); // NOI18N
        ////
        ////        Map rewrite = importStyleResources(depth, doc, ss);
        Map rewrite = getStyleResources(url, depth);
        
        URL oldBase = context.base;
        context.base = url;
        context.base = oldBase;
        
        try {
            String newStylesheet = replaceStrings(rewrite, read(url));
            
            String name = urlString.substring(urlString.lastIndexOf('/') + 1);
            
            if (name.indexOf('.') == -1) {
                name = name + ".css"; // NOI18N
            }
            
            // TODO - store in the resources folder instead
            FileObject webFolder = null;
            webFolder = JsfProjectUtils.getDocumentRoot(context.project);
            
            File targetFile = new File(FileUtil.toFile(webFolder), name);
            
            if (targetFile.exists()) {
                AddResourceOverwriteDialog d = new AddResourceOverwriteDialog(targetFile);
                d.showDialog();
                
                File newTarget = d.getFile();
                
                if (newTarget == null) {
                    // User pressed cancel - just use the original name
                    return name;
                }
                
                if (newTarget.exists()) {
                    //return linkRef;
                    return name;
                }
                
                name = newTarget.getName();
                
                /*
                if (mimeDir != null) {
                    linkRef = mimeDir + "/" + fileName;  // NOI18N
                } else {
                    linkRef = fileName;
                }
                 */
            }
            
            DataObject ssdo = addSource(webFolder, newStylesheet, name);
            String s = ssdo.getPrimaryFile().getNameExt();
            
            // XXX get full name
            String relPath = s;
            
            return relPath;
        } catch (IOException ex) {
            if (context.warnMissingFile) {
                //                InSyncService.getProvider().getRaveErrorHandler().displayError(NbBundle.getMessage(ImportPagePanel.class,
                //                        "NoSuchResource", ex.getLocalizedMessage() + ": " + urlString)); // NOI18N
                IllegalStateException ise = new IllegalStateException(ex);
                Throwable th = ErrorManager.getDefault().annotate(ise, NbBundle.getMessage(ImportPagePanel.class, "NoSuchResource", ex.getLocalizedMessage() + ": " + urlString));
                ErrorManager.getDefault().notify(ErrorManager.WARNING, th);
            } else {
                ErrorManager.getDefault().notify(ex);
            }
        }
        
        /*
        // Now I just need to change the resourceURL to something temporary
        // that I can copy from
        try {
            URL resourceURL = url;
            String projectUrl = WebAppProject.addResource(webform, resourceURL, true);
            element.setAttribute(urlAttribute, projectUrl);
            return projectUrl;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
         */
        return null;
    }
    
    private static Document getDom(FileObject fo) {
        MarkupUnit unit = getMarkupUnit(fo);
        
        if (unit == null) { // possible when project has closed
            
            return null;
        }
        
        return unit.getSourceDom();
    }
    
    
    
    private static MarkupUnit getMarkupUnit(FileObject fo) {
        FacesModel model = getModel(fo);
        if(model == null) {
            return null;
        }
        
        return model.getMarkupUnit();
    }
    
    
    
    /** Remove form beans in the document except for the given set of forms to keep */
    private void removeBogusForms(List keepForms, FileObject webformFile) {
        // Remove all form beans that are "bogus" (empty)
        // Also make sure that the FacesPageUnit has the correct default parent
        // after this (since it may currently be using the bogus)
        // Strategy: locate all form tags in the hierarchy: those we didn't add
        // via import should be yanked.
        MarkupBean defaultParent = getModel(webformFile).getFacesUnit().getDefaultParent();
        ArrayList allForms = new ArrayList();
        findForms(allForms, getModel(webformFile).getRootBean());
        
        Iterator it = allForms.iterator();
        
        while (it.hasNext()) {
            DesignBean bean = (DesignBean)it.next();
            
            if (!keepForms.contains(bean)) {
                // <move>
                //                if (FacesSupport.getMarkupBean(bean) == defaultParent) {
                // ====
                if (getMarkupBean(bean) == defaultParent) {
                    // </move>
                    defaultParent = null;
                }
                
                getModel(webformFile).getLiveUnit().deleteBean(bean);
            } else {
                // <move>
                //                Element e = FacesSupport.getElement(bean);
                // ====
                Element e = getElement(bean);
                // </move>
                
                if (e != null) {
                    e.setAttribute(HtmlAttribute.ID, bean.getInstanceName());
                }
            }
        }
        
        if (defaultParent == null) {
            // We've deleted the bean that used to be the parent so set a new parent
            // Pick a suitable candidate.... let's pick the bean with the largest number
            // of children (recursively). The rationale is that this probably includes
            // the most content... so if we have a main form and a smaller form for
            // say search, we pick the main form. Another possible selection criterion
            // would be hierarchy depth; pick form tags closer to body than deeply
            // nested forms.
            int fewestChildren = Integer.MAX_VALUE;
            it = keepForms.iterator();
            
            while (it.hasNext()) {
                DesignBean bean = (DesignBean)it.next();
                // <move>
                //                MarkupBean mb = FacesSupport.getMarkupBean(bean);
                // ====
                MarkupBean mb = getMarkupBean(bean);
                // </move>
                
                if (mb != null) {
                    int children = getChildCount(mb.getElement());
                    
                    if (children < fewestChildren) {
                        defaultParent = mb;
                    }
                }
            }
            
            getModel(webformFile).getFacesUnit().setDefaultParent(defaultParent);
        }
    }
    
    
    private DesignBean replaceComponent(FileObject webformFile, Element element, String className) {
        return replaceComponent(webformFile, element, className, true);
    }
    
    private DesignBean replaceComponent(FileObject webformFile, Element element, String className,
            boolean copyChildren) {
        LiveUnit unit = getModel(webformFile).getLiveUnit();
        Node under = element.getParentNode();
        Node before = element;
        MarkupPosition pos = new MarkupPosition(under, before);
        
        DesignBean parent = null;
        Node n = under;
        
        //        while (n instanceof RaveElement) {
        //            RaveElement xel = (RaveElement)n;
        while (n instanceof Element) {
            Element xel = (Element)n;
            
            //            if (xel.getDesignBean() != null) {
            //                DesignBean lbean = (DesignBean)xel.getDesignBean();
            DesignBean lbean = InSyncService.getProvider().getMarkupDesignBeanForElement(xel);
            if (lbean != null) {
                if (lbean.isContainer()) {
                    parent = lbean;
                    
                    break;
                }
            }
            
            n = n.getParentNode();
        }
        
        DesignBean bean = unit.createBean(className, parent, pos);
        
        // Move the element's children to the new bean
        if (bean != null) {
            if (copyChildren) {
                // <move>
                //                Element e = FacesSupport.getElement(bean);
                // ====
                Element e = getElement(bean);
                // </move>
                
                if (e != null) {
                    NodeList nl = element.getChildNodes();
                    int num = nl.getLength();
                    
                    if (num > 0) {
                        // Copy list first since nodelist object changes
                        // under us when we move the nodes
                        ArrayList children = new ArrayList(num);
                        
                        for (int i = 0; i < num; i++) {
                            children.add(nl.item(i));
                        }
                        
                        for (int i = 0; i < num; i++) {
                            e.appendChild((Node)children.get(i));
                        }
                    }
                }
            }
            
            // Get rid of the old element that we've replaced!
            /*
            // This doesn't work as expected because deleteBean on the old
            // element will see that a component of id "foo" is being removed
            // and it will therefore remove references to "foo" in other components
            // like the "for" property of associated labels. Instead we rely on a
            // sync when the import is done.
            DesignBean oldBean = null;
            if (element instanceof XhtmlElement) {
                oldBean = ((XhtmlElement)element).getDesignBean();
            }
            if (oldBean != null) {
                unit.deleteBean(oldBean);
            } else {
                under.removeChild(element);
            }
             */
            under.removeChild(element);
        }
        
        // TODO: remove the element since it now has a sibling!
        return bean;
    }
    
    
    
    private void convertProperty(DesignBean bean, Element element, String htmlAttr, String jsfProp) {
        if ((bean == null) || (element == null)) {
            return;
        }
        
        if (!element.hasAttribute(htmlAttr)) {
            return;
        }
        
        String value = element.getAttribute(htmlAttr);
        DesignProperty prop = bean.getProperty(jsfProp);
        
        if (prop == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING,
                    "No such jsf property alias " + jsfProp + " in bean " + bean.getInstanceName() + " for html attribute " + htmlAttr); // NOI18N
            
            return;
        }
        
        // XXX Do any kind of to-Object conversion here?? Do we have any
        // cases where we have to convert from e.g. a string color description
        // in HTML to an actual Color object (for example) in the JSF
        // component?
        try {
            PropertyDescriptor pd = prop.getPropertyDescriptor();
            
            if (pd != null) {
                if (pd.getPropertyType() == Integer.TYPE) {
                    try {
                        int f = Integer.parseInt(value);
                        prop.setValue(new Integer(f));
                        
                        return;
                    } catch (NumberFormatException nfe) {
                        ErrorManager.getDefault().notify(nfe);
                    }
                } else if (pd.getPropertyType() == Boolean.TYPE) {
                    // True if "yes", "on", "true", "1", or <name>
                    // (e.g.  checked="checked" is considered true)
                    if (value.equalsIgnoreCase("yes") || // NOI18N
                            value.equalsIgnoreCase("on") || // NOI18N
                            value.equalsIgnoreCase("true") || // NOI18N
                            value.equalsIgnoreCase("1") || // NOI18N
                            value.equalsIgnoreCase(jsfProp)) {
                        prop.setValue(Boolean.TRUE);
                    } else {
                        assert value.equalsIgnoreCase("no") || // NOI18N
                                value.equalsIgnoreCase("off") || // NOI18N
                                value.equalsIgnoreCase("false") || // NOI18N
                                value.equalsIgnoreCase("0") || // NOI18N
                                value.equalsIgnoreCase(""); // NOI18N
                        prop.setValue(Boolean.FALSE);
                    }
                    
                    return;
                }
            }
            
            prop.setValue(value);
        } catch (Exception ex) { // Don't let one-value assignment stop whole conversion
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    /** For the given select element, find all option children and
     * return as an array of strings */
    private String[] extractOptions(Element element) {
        Vector v = new Vector();
        // <move>
        //        int[] selected = FormComponentBox.populateOptions(element, v, context.webform.getMarkup());
        // ====
        int[] selected = populateOptions(element, v, getMarkupUnit(context.webformFile));
        // </move>
        
        // XXX Can I somehow preserve the selection into the data?
        if (v.size() == 0) {
            return new String[0];
        }
        
        return (String[])v.toArray(new String[v.size()]);
    }
    
    private DesignBean replaceTag(FileObject webformFile, Element element, String className) {
        boolean copyChildren = true;
        LiveUnit unit = getModel(webformFile).getLiveUnit();
        Node under = element.getParentNode();
        Node before = element;
        MarkupPosition pos = new MarkupPosition(under, before);
        
        DesignBean parent = null;
        Node n = under;
        
        //        while (n instanceof RaveElement) {
        //            RaveElement xel = (RaveElement)n;
        while (n instanceof Element) {
            Element xel = (Element)n;
            
            //            if (xel.getDesignBean() != null) {
            //                DesignBean lbean = (DesignBean)xel.getDesignBean();
            DesignBean lbean = InSyncService.getProvider().getMarkupDesignBeanForElement(xel);
            if (lbean != null) {
                if (lbean.isContainer()) {
                    parent = lbean;
                    
                    break;
                }
            }
            
            n = n.getParentNode();
        }
        
        DesignBean bean = unit.createBean(className, parent, pos);
        
        // Move the element's children to the new bean
        if (bean != null) {
            // Copy all the attributes!!
            // <move>
            //            Element e = FacesSupport.getElement(bean);
            // ====
            Element e = getElement(bean);
            // </move>
            
            if (e != null) {
                int numAttr = element.getAttributes().getLength();
                
                for (int i = 0; i < numAttr; i++) {
                    Node a = element.getAttributes().item(i); // XXX move element.getAttributes out of loop
                    String attribute = a.getNodeName();
                    
                    if (attribute.equals(FacesBean.BINDING_ATTR)) {
                        // Don't mess with component binding!!!
                        continue;
                    }
                    
                    String value = a.getNodeValue();
                    e.setAttribute(a.getNodeName(), a.getNodeValue());
                    
                    DesignProperty prop = bean.getProperty(attribute);
                    
                    try {
                        if (prop != null) {
                            prop.setValue(value);
                        }
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
                
                if (copyChildren) {
                    NodeList nl = element.getChildNodes();
                    int num = nl.getLength();
                    
                    if (num > 0) {
                        // Copy list first since nodelist object changes
                        // under us when we move the nodes
                        ArrayList children = new ArrayList(num);
                        
                        for (int i = 0; i < num; i++) {
                            children.add(nl.item(i));
                        }
                        
                        for (int i = 0; i < num; i++) {
                            e.appendChild((Node)children.get(i));
                        }
                    }
                }
            }
            
            // Get rid of the old element that we've replaced!
            under.removeChild(element);
        }
        
        // TODO: remove the element since it now has a sibling!
        return bean;
    }
    
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static Element getElement(DesignBean lb) {
        if (lb instanceof MarkupDesignBean) {
            return ((MarkupDesignBean)lb).getElement();
        } else {
            return null;
        }
    }
    
    
    
    /** XXX Copy from designer FormComponentBox, it shouldn't be neither in designer not here. */
    private static int[] populateOptions(Element element, Vector v, MarkupUnit markup) {
        // <markup_separation>
        //        MarkupService markupService = MarkupServiceProvider.getDefault();
        // </markup_separation>
        ArrayList selected = new ArrayList();
        NodeList list = element.getChildNodes();
        int len = list.getLength();
        
        for (int i = 0; i < len; i++) {
            Node child = list.item(i);
            
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            Element option = (Element)child;
            
            if (!option.getTagName().equals(HtmlTag.OPTION.getTagName())) {
                continue;
            }
            
            // Found an option
            NodeList list2 = option.getChildNodes();
            int len2 = list2.getLength();
            StringBuffer sb = new StringBuffer();
            
            for (int j = 0; j < len2; j++) {
                Node child2 = list2.item(j);
                
                if ((child2.getNodeType() != Node.TEXT_NODE) &&
                        (child2.getNodeType() != Node.CDATA_SECTION_NODE)) {
                    continue;
                }
                
                String nodeVal = child2.getNodeValue();
                
                if (nodeVal != null) {
                    nodeVal = nodeVal.trim();
                    
                    //                    RaveText textNode = (child2 instanceof RaveText) ? (RaveText)child2 : null;
                    Text textNode = (child2 instanceof Text) ? (Text)child2 : null;
                    
                    //                    if ((textNode != null) && textNode.isJspx()) {
                    if (textNode != null && MarkupService.isJspxNode(textNode)) {
                        // <markup_separation>
                        //                        nodeVal = markupService.expandHtmlEntities(nodeVal, true, element);
                        // ====
                        nodeVal = InSyncService.getProvider().expandHtmlEntities(nodeVal, true, element);
                        // </markup_separation>
                    } // ELSE: regular entity fixing?
                    
                    sb.append(nodeVal);
                    
                    // XXX I should be able to bail here - for combo
                    // boxes I only show the first item! (There's no
                    // way for the user to open the menu). However,
                    // for things like a multi select, you need to
                    // show possibly multiple choices, so perhaps pass
                    // in a max count?
                }
            }
            
            if (sb.length() > 0) {
                // Is this item selected too?
                Attr attr = option.getAttributeNode(HtmlAttribute.SELECTED);
                
                if (attr != null) {
                    selected.add(new Integer(v.size()));
                }
                
                v.addElement(sb.toString());
            }
        }
        
        if (selected != null) {
            int[] result = new int[selected.size()];
            
            for (int i = 0, n = selected.size(); i < n; i++) {
                result[i] = ((Integer)selected.get(i)).intValue();
            }
            
            return result;
        }
        
        return null;
    }
    
    
    
    private void initPaletteComponents() {
        if (context.paletteComponents != null) {
            return;
        }
        
        context.paletteComponents = new HashMap(200);
        
        //        // Iterate over the palette contents and locate all available components
        //        // such that I can create a reverse map, from tag name to component name.
        //        // This is used in component import to create binding attributes
        //        // to concrete components, since JSPs being imported may not contain these
        //        // (page import doesn't study java code, and besides, JSF doesn't require
        //        // component binding but we always need it.)
        //        Palette[] palettes = PaletteComponentModel.getInstance().getPalettes();
        //        for (int i = 0; palettes != null && i < palettes.length; i++) {
        //            PaletteSection[] paletteSections = palettes[i].getPaletteSections();
        //            for (int j = 0; paletteSections != null && j < paletteSections.length; j++) {
        //                PaletteItem[] paletteItems = paletteSections[j].getItems();
        //                for (int k = 0; paletteItems != null && k < paletteItems.length; k++) {
        //                    PaletteItem pi = paletteItems[k];
        //
        //                    if (pi instanceof BeanPaletteItem) {
        //                        BeanPaletteItem bpi = (BeanPaletteItem)pi;
        //
        //                        // getBeanClassName() seems to do more work than it should!!
        //                        String className = bpi.getName();
        //                        BeanInfo beanInfo = BeanPaletteItem.loadBeanInfo(className);
        //
        //                        if (beanInfo != null) {
        //                            String tagName = FacesUnit.getBeanTagName(beanInfo);
        //
        //                            if ((tagName != null) && (tagName.length() > 0)) {
        //                                context.paletteComponents.put(tagName, className);
        //                            }
        //                        }
        //                    }
        //                }
        //            }
        //        }
        // TODO The old way is not working now, there needs to be new API made.
        //        context.paletteComponents = Complib.getTagName2ClassNameMap(context.project);
        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                new IllegalStateException("Missing API (from complib?) to provide map with tag names to class names for project, project=" + context.project)); // TEMP
        context.paletteComponents = Collections.EMPTY_MAP;
    }
    
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static boolean isValueBindingExpression(String s, boolean containsOk) {
        assert s != null;
        
        // TODO: Use
        //  ((FacesDesignProperty)designProperty).isBound()
        // instead - so change to passing in a DesignProperty etc.
        if (containsOk) {
            return s.indexOf("#{") != -1; // NOI18N
        } else {
            return s.startsWith("#{"); // NOI18N
        }
    }
    
    
    private Map getStyleResourcesForElement(Element element, String rules) {
        //        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);
        
        //        URL docUrl = InSyncService.getProvider().getUrl(doc);
        //        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
        //        StyleDeclaration sd = CssProvider.getEngineService().parseStyleDeclarationForElement(element, rules);
        //        String[] urlStrings = getStyleResourcesFromStyleDeclaration(sd);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        String[] urlStrings = CssProvider.getEngineService().getStyleResourcesForElement(
                element,
                rules,
                doc,
                docUrl,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(urlStrings);
    }
    
    private Map getStyleResources(String rules, int depth) {
        //        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);
        
        //        URL docUrl = InSyncService.getProvider().getUrl(doc);
        //        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
        //        InputSource is = new InputSource(new StringReader(rules));
        //        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, context.base, "all", context.base); // NOI18N
        //        ResourceData[] resourceData = getStyleResourcesFromStyleSheet(doc, ss);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        ResourceData[] resourceData = CssProvider.getEngineService().getStyleResourcesForRules(
                rules,
                doc,
                docUrl,
                context.base,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(depth, resourceData);
    }
    
    private Map getStyleResources(URL url, int depth) {
        //        Document doc = MarkupUnit.createEmptyDocument(true);
        Document doc = createEmptyDocument();
        InSyncService.getProvider().setUrl(doc, context.base);
        
        //        URL docUrl = InSyncService.getProvider().getUrl(doc);
        //        CssProvider.getEngineService().createCssEngineForDocument(doc, docUrl);
        //        InputSource is = new InputSource(url.toString());
        //        StyleSheet ss = CssProvider.getEngineService().parseStyleSheetForDocument(doc, is, url, "all", url); // NOI18N
        //        ResourceData[] resourceData = getStyleResourcesFromStyleSheet(doc, ss);
        URL docUrl = InSyncService.getProvider().getUrl(doc);
        ResourceData[] resourceData = CssProvider.getEngineService().getStyleResourcesForUrl(
                url,
                doc,
                docUrl,
                new int[] {XhtmlCss.BACKGROUND_IMAGE_INDEX, XhtmlCss.LIST_STYLE_IMAGE_INDEX});
        
        return importStyleResources(depth, resourceData);
    }
    
    /** Replace the keys in oldStylesheet with values from the hashmap. */
    private String replaceStrings(Map rewrite, String oldStylesheet) {
        Set keySet = rewrite.keySet();
        String newStylesheet = oldStylesheet;
        
        if (keySet.size() > 0) {
            String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
            String[] values = new String[keys.length];
            
            for (int i = 0; i < keys.length; i++) {
                values[i] = (String)rewrite.get(keys[i]);
            }
            
            StringBuffer sb = new StringBuffer(oldStylesheet.length() + 200);
            int m = keys.length;
            outer:
                for (int i = 0, n = oldStylesheet.length(); i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        if (oldStylesheet.startsWith(keys[j], i)) {
                            // Replace here!
                            sb.append(values[j]);
                            i += (keys[j].length() - 1); // -1: since we're going to add in for loop
                            
                            continue outer;
                        }
                    }
                    
                    sb.append(oldStylesheet.charAt(i));
                }
                
                newStylesheet = sb.toString();
        }
        
        return newStylesheet;
    }
    
    private String read(URL url) throws IOException {
        StringBuffer sb = new StringBuffer(1000); // XXX is there a utility method for this?
        InputStream is = null;
        BufferedInputStream in = null;
        
        try {
            is = url.openStream();
            in = new BufferedInputStream(is);
            
            int c;
            
            while ((c = in.read()) != -1)
                sb.append((char)c);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
        
        return sb.toString();
    }
    
    
    
    
    
    /** Based closely on WebAppProject.addResource, but adds to the source folder instead. (The "source" folder can be anything, not necessary just the form folder. I should change the name -- TODO). */
    private DataObject addSource(FileObject formFolderFO, String contents, String name)
            throws IOException {
        //GenericFolder formFolder = (GenericFolder)webForm.getParent();
        //DataObject formFolderDO = formFolder.getDataObject();
        //FileObject formFolderFO = formFolderDO.getPrimaryFile();
        InputStream is = null;
        OutputStream os = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        FileLock lock = null;
        
        try {
            is = new StringBufferInputStream(contents);
            
            FileObject resourceFO = null;
            resourceFO = formFolderFO.getFileObject(name);
            
            if (resourceFO == null) {
                resourceFO = formFolderFO.createData(name);
            }
            
            lock = resourceFO.lock();
            os = resourceFO.getOutputStream(lock);
            in = new BufferedInputStream(is);
            out = new BufferedOutputStream(os);
            
            int c;
            
            while ((c = in.read()) != -1)
                out.write(c);
            
            DataObject dobj = DataObject.find(resourceFO);
            
            return dobj;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (lock != null) {
                    lock.releaseLock();
                }
                
                if (in != null) {
                    in.close();
                }
                
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
    }
    
    
    
    /** Locate all UIForm beans in the component tree */
    private void findForms(List list, DesignBean bean) {
        if (bean.getInstance() instanceof UIForm) {
            list.add(bean);
        }
        
        for (int i = 0, n = bean.getChildBeanCount(); i < n; i++) {
            findForms(list, bean.getChildBean(i));
        }
    }
    
    
    /** XXX Copy from designer FacesSupport, it shouldn't be neither in designer not here. */
    private static MarkupBean getMarkupBean(DesignBean lb) {
        if (!(lb instanceof BeansDesignBean)) {
            return null;
        }
        
        Bean b = ((BeansDesignBean)lb).getBean();
        
        if (b instanceof MarkupBean) {
            return (MarkupBean)b;
        }
        
        return null;
    }
    
    
    private int getChildCount(Node n) {
        int count = 1; // me
        NodeList nl = n.getChildNodes();
        
        for (int i = 0; i < nl.getLength(); i++) {
            count += getChildCount(nl.item(i));
        }
        
        return count;
    }
    
    
    // Moved from insync/MarkupUnit.
    private static Document createEmptyDocument() {
        try {
            org.xml.sax.InputSource is =
                    new org.xml.sax.InputSource(new StringReader("<html><body><p/></body></html>"));
            DocumentBuilder parser = MarkupService.createRaveSourceDocumentBuilder(true);
            Document doc = parser.parse(is);
            return doc;
        } catch (java.io.IOException e) {
            // should not happen reading from a string!
            //            Trace.trace("insync.markup", "Error in createEmptyDocument");
            //            Trace.trace("insync.markup", e);
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            //            Trace.trace("insync.markup", "Error in createEmptyDocument");
            //            Trace.trace("insync.markup", e);
            e.printStackTrace();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            //            Trace.trace("insync.markup", "Error in createEmptyDocument");
            //            Trace.trace("insync.markup", e);
            e.printStackTrace();
        }
        return null;
    }
    
    private Map importStyleResources(String[] urlStrings) {
        Map rewrite = new HashMap();
        
        for(int i = 0; i < urlStrings.length; i++) {
            String urlString = urlStrings[i];
            // Import the image, as newUrl
            String projectUrl = copyResource(urlString);
            if (projectUrl != null) {
                rewrite.put(urlString, projectUrl);
            }
        }
        return rewrite;
    }
    
    private Map importStyleResources(int depth, ResourceData[] resourceData) {
        Map rewrite = new HashMap();
        for (int i = 0; i < resourceData.length; i++) {
            ResourceData rd = resourceData[i];
            if (rd instanceof UrlStringsResourceData) {
                UrlStringsResourceData urlStringsResourceData = (UrlStringsResourceData)rd;
                rewrite.putAll(importStyleResources(urlStringsResourceData.getUrlStrings()));
            } else if (rd instanceof UrlResourceData) {
                UrlResourceData urlResourceData = (UrlResourceData)rd;
                String relPath;
                try {
                    relPath = importStyleSheetResource(depth, urlResourceData.getUrl(), urlResourceData.getUrlString());
                } catch (MalformedURLException mfu) {
                    // XXX shouldn't happen
                    ErrorManager.getDefault().notify(mfu);
                    return rewrite;
                }
                
                if (relPath != null) {
                    rewrite.put(urlResourceData.getUrlString(), relPath);
                }
            } else {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                        new IllegalStateException("Unexpected resourceData=" + rd)); // NOI18N
            }
        }
        return rewrite;
    }
    
    private String importStyleSheetResource(int depth, URL url, String urlString) throws MalformedURLException {
        String parent = new File(url.getPath()).getParent() + "/";
        URL oldBase = context.base;
        
        context.base = new URL(url.getProtocol(), url.getHost(), url.getPort(), parent);
        
        //        String urlString = mr.getRelativeUri();
        String relPath = handleStyleSheet(depth + 1, urlString, url);
        context.base = oldBase;
        return relPath;
    }
    
}

