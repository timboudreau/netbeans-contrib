/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.zeroadmin;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import org.w3c.dom.events.*;
import org.apache.xml.serialize.*;

import org.openide.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListener;
import org.openide.util.Lookup;
import org.openide.xml.*;

/** 
 * The code in this class is essentially stolen from the apisupport module.
 * It is adapted to be used with XMLBufferFilesystem.
 * Parse support for XML layers.
 * Implements the connection between a text document and a DOM
 * document. When the DOM changes, changes the text.
 * Substantially stolen from org.apache.tools.ant.module.xml.AntProjectSupport
 * which does something rather similar.
 * @author Jesse Glick, David Strupl
 */
public class ParseRegen implements org.w3c.dom.events.EventListener,
                                   Runnable,
                                   ErrorHandler {
  
    private transient char[] buffer;

    private transient Document layerDoc = null; // [PENDING] SoftReference
    private transient Throwable exception = null;
    private transient boolean parsed = false;
    private transient String systemId;
    transient RequestProcessor.Task regenTask;
    private static boolean validating = true;
    
    // DocumentBuilderFactory or IllegalStateException
    private static Object parserfact = null;
    
    private static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.core.projects"); // NOI18N
    
    public ParseRegen (char[] buffer, String systemId) {
        this(buffer);
        this.systemId = systemId;
    }

    public ParseRegen (char[] buffer, String systemId, boolean validate) {
        this(buffer);
        this.systemId = systemId;
        validating = validate;
    }

    public ParseRegen (char[] buffer) {
        this.buffer = buffer;
    }
    
    public synchronized Document getDocument () {
        if (!parsed) {
            parseDocument ();
        }
        return layerDoc;
    }
    
    public synchronized Throwable getParseException () {
        if (!parsed) {
            parseDocument ();
        }
        return exception;
    }

    public synchronized char[] getBuffer() {
        return buffer;
    }
    
    public static final class NoSuchDocumentException extends NullPointerException {
        public NoSuchDocumentException(String m) {
            super(m);
        }
    }
    
    /** Attempt to find a usable DOM parser.
     * First tries JAXP. But if that produces a parser that does not understand
     * the DOM Event Model - for example, Crimson - we revert to Xerces.
     * @return a DOM parser understanding the DOM event model
     * @throws IllegalStateException if no such parser can be found
     * @see "#20270"
     */
    private static synchronized DocumentBuilderFactory findParser() throws IllegalStateException {
        if (parserfact == null) {
            try {
                DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
                if (fact.newDocumentBuilder().newDocument() instanceof EventTarget) {
                    err.log("JAXP found a usable DOM parser: " + fact);
                } else {
                    err.log("JAXP failed, trying Xerces impl");
                    // Can't use this ClassLoader, since the core will block it
                    // (we have no dep on Xerces).
                    Class xercesImpl = null;
                    try {
                        xercesImpl = Class.forName("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", true, ClassLoader.getSystemClassLoader());
                    } catch (ClassNotFoundException cnfe) {
                        // so try another classloader
                        xercesImpl = Class.forName("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", true, (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class));
                    }
                    fact = (DocumentBuilderFactory)xercesImpl.newInstance();
                }
                fact.setValidating(validating);
                parserfact = fact;
            } catch (Exception e) {
                parserfact = new IllegalStateException(e.toString());
                err.annotate((IllegalStateException)parserfact, e);
            } catch (FactoryConfigurationError fce) {
                parserfact = new IllegalStateException(fce.toString());
                err.annotate((IllegalStateException)parserfact, fce);
            }
        }
        if (parserfact instanceof IllegalStateException) {
            throw (IllegalStateException)parserfact;
        } else {
            return (DocumentBuilderFactory)parserfact;
        }
    }
    
    private void parseDocument () {
        err.log ("ParseRegen.parseDocument: " + this);
        try {
            DocumentBuilder parser = findParser().newDocumentBuilder();
            parser.setEntityResolver (EntityCatalog.getDefault ());
            parser.setErrorHandler (this);
            Reader rd;
            if (buffer != null) {
                rd = new CharArrayReader(buffer);
            } else {
                throw new NoSuchDocumentException("no valid file to parse");
            }
            try {
                InputSource in = new InputSource (rd);
                if (systemId != null) {
                    in.setSystemId(systemId);
                }
                Document doc = parser.parse(in);
                {
                    // XXX Hack for a weird Xerces 2.0.1 parser bug. It parses a
                    // <!DOCTYPE> and then includes in the document any comments
                    // found in the DTD as if they were in the XML document!
                    // Unfortunately this workaround causes the regen of XML to
                    // lose any comments between the DOCTYPE and the root element
                    // (in addition to other unavoidable problems with parse-genen
                    // logic, well documented in Ant module bugs).
                    NodeList nl = doc.getChildNodes();
                    List l = new LinkedList(); // List<Comment>
                    boolean foundDoctype = false;
                    for (int i = 0; i < nl.getLength(); i++) {
                        if (foundDoctype) {
                            if (nl.item(i) instanceof Comment) {
                                l.add(nl.item(i));
                            } else if (nl.item(i) instanceof Element) {
                                break;
                            }
                        } else {
                            if (nl.item(i) instanceof DocumentType) {
                                foundDoctype = true;
                            }
                        }
                    }
                    if (!l.isEmpty()) {
                        if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                            err.log("ParseRegen: Xerces workaround, removing bogus inserts: " + l);
                        }
                        Iterator it = l.iterator();
                        while (it.hasNext()) {
                            doc.removeChild((Comment)it.next());
                        }
                    }
                }
                
                EventTarget targ = (EventTarget) doc;
                targ.addEventListener ("DOMSubtreeModified", this, false); // NOI18N
                targ.addEventListener ("DOMAttrModified", this, false); // NOI18N

                layerDoc = doc;
                if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                    StringWriter wr = new StringWriter(1000);
                    OutputFormat form = new OutputFormat(layerDoc, "UTF-8", false);
                    form.setPreserveSpace(true);
                    try {
                        new XMLSerializer(wr, form).serialize(layerDoc);
                        wr.close();
                        err.log("ParseRegen.parseDocument:\n" + wr);
                    } catch (IOException ioe) {
                        err.notify(ErrorManager.INFORMATIONAL, ioe);
                    }
                }
                exception = null;
            } finally {
                rd.close ();
            }
        } catch (Exception e) {
            // leave layerDoc the way it is...
            exception = e;
        }
        parsed = true;
    }
    
    // Report everything as fatal, there is no reason to be sloppy.
    // It is necessary specifically to implement error() correctly
    // in order to receive notification that the DTD has been violated.
    public void error (SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
    public void warning (SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
    public void fatalError (SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
    
    private synchronized void regenerate () {
        err.log("ParseRegen.regenerate: " + this);
        if (layerDoc == null) throw new IllegalStateException ();
        try {
                OutputFormat format = new OutputFormat(layerDoc, "UTF-8", false); 
                format.setPreserveSpace (true);
                CharArrayWriter wr = null;
                try {
                    wr = new java.io.CharArrayWriter();
                    XMLSerializer ser = new XMLSerializer (wr, format);
                    ser.serialize (layerDoc);
                    // Apache serializer also fails to include trailing newline, sigh.
                    wr.write ('\n');
                } finally {
                    if (wr != null) {
                        wr.close ();
                    }
                }
                exception = null;
                buffer = wr.toCharArray();
                parsed = true;
        } catch (IOException ioe) {
            exception = ioe;
        }
    }
  
    public void handleEvent (org.w3c.dom.events.Event ev) {
        if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
            err.log ("ParseRegen.handleEvent: " + this);
            //Thread.dumpStack ();
            err.log("\tev=" + ev);
            err.log("\tev.type=" + ev.getType ());
            err.log("\tev.target=" + ev.getTarget ());
            StringWriter wr = new StringWriter(1000);
            OutputFormat form = new OutputFormat(layerDoc, "UTF-8", false);
            form.setPreserveSpace(true);
            try {
                new XMLSerializer(wr, form).serialize(layerDoc);
                wr.close();
                err.log("\tdoc:\n" + wr);
            } catch (IOException ioe) {
                err.notify(ErrorManager.INFORMATIONAL, ioe);
            }
        }
        // Make sure we regenerate from the same DOM tree that is current!
        if (exception != null || ev.getCurrentTarget () != layerDoc) {
            // Attempt to modify stale DOM tree -> ignore it and return.
            // Ideally would cancel ev, but DOM2 does not support
            // cancelling mutation events, so just give up.
            err.log(ErrorManager.WARNING, "WARNING: ParseRegen.handleEvent on stale DOM tree");
            return;
        }
        if (regenTask == null) {
            regenTask = ZeroAdminModule.RP.create(this);
        }
        regenTask.schedule(10);
    }
    
    public void run () {
        regenerate ();
    }
    
}
