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

package org.netbeans.modules.zeroadmin;

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.*;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem;
import org.openide.util.*;
import org.openide.util.enum.EmptyEnumeration;
import org.openide.util.io.*;

import org.w3c.dom.*;
import org.w3c.dom.events.*;

/**
 * The code in this class is essentially stolen from the apisupport module.
 * A filesystem which is based on a DOM document and implements
 * the same syntax as XMLFileSystem, from which inspiration is taken.
 * Not implemented similarly to XMLFileSystem because this is writable
 * and designed specifically to write human-readable XML and work nicely
 * as an authoring tool. The filesystem expects to get an XML document
 * according to DTD "-//NetBeans//DTD Filesystem 1.0//EN" (or 1.1 is OK)
 * When it is changed via FileSystems API, it will fire DOM
 * mutation events. The document is expected
 * to be encoded using UTF-8 encoding.
 * @author Jesse Glick, adapted by David Strupl
 */
public class XMLBufferFileSystem extends AbstractFileSystem implements AbstractFileSystem.Attr, AbstractFileSystem.Change, AbstractFileSystem.Info, AbstractFileSystem.List, AbstractFileSystem.Transfer, FileSystem.Status, FileChangeListener {
    
    private Document doc;
    private Date time;
    private FileChangeListener fileChangeListener;
    private ParseRegen generator;
    
    private static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.core.projects"); // NOI18N
    
    private static final ClassLoader classLoader = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
    
    private static final String BINARY_ATTR_NAME = "XMLBufferFileSystem.binary";
    
    // enlarged to not need do the test for negative byte values
    private final static char[] HEXDIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
                                             '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                                             '0', '1', '2', '3', '4', '5', '6', '7'};
    
    private static final int INDENT = 8;
    private static final int BLOCK = 100;
    private static final int BUFFSIZE = INDENT + BLOCK;
    
    private static final String EMPTY_BUFFER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE filesystem PUBLIC \"-//NetBeans//DTD Filesystem 1.0//EN\" \"http://www.netbeans.org/dtds/filesystem-1_0.dtd\"><filesystem></filesystem>";

    public XMLBufferFileSystem() {
        this(new ParseRegen(EMPTY_BUFFER.toCharArray()));
    }
    
    public XMLBufferFileSystem(ParseRegen gen) {
        this.attr = this;
        this.change = this;
        this.info = this;
        this.list = this;
        this.transfer = this;
        this.generator = gen;
        this.doc = generator.getDocument();
        time = new Date ();
        fileChangeListener = WeakListener.fileChange (this, null);
    }
    
    public Status getStatus () {
        return this;
    }
    
    private void writeObject (ObjectOutputStream out) throws IOException {
        throw new NotSerializableException ("XMLBufferFileSystem is not persistent");
    }
    
    public Document getDocument () {
        return doc;
    }

    public void setDocument (Document doc) {
        this.doc = doc;
        Enumeration e = existingFileObjects (getRoot ());
        while (e.hasMoreElements ()) {
            ((FileObject) e.nextElement ()).refresh (true);
        }
        time = new Date ();
    }
    
    public String getDisplayName () {
        return "Writable XML FS: " + getSystemName ();
    }
    
    public boolean isReadOnly () {
        return false;
    }
    
    public char[] getBuffer() {
        return generator.getBuffer();
    }
    
    public void setBuffer(char [] buf) {
        generator = new ParseRegen(buf);
        setDocument(generator.getDocument());
    }
    
    public void waitFinished() {
        if (generator != null) {
            RequestProcessor.Task t = generator.regenTask;
            if (t != null) {
                t.waitFinished();
            }
        }
    }
    
    /** Given a resource name, find the matching DOM element.
     * @return a <folder> or <file> or <filesystem> element, or null if file does not exist
     */
    private Element findElement (String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return findElementIn (doc.getDocumentElement (), name);
    }
    /** helper method only */
    private Element findElementIn (Element el, String name) {
        if (el == null) return null;
        if (name.equals ("")) { // NOI18N
            return el;
        } else {
            int idx = name.indexOf ((char) '/');
            String nextName, remainder;
            if (idx == -1) {
                nextName = name;
                remainder = ""; // NOI18N
            } else {
                nextName = name.substring (0, idx);
                remainder = name.substring (idx + 1);
            }
            Element subel = null;
            NodeList nl = el.getChildNodes ();
            for (int i = 0; i < nl.getLength (); i++) {
                if (nl.item (i).getNodeType () != Node.ELEMENT_NODE) continue;
                Element e = (Element) nl.item (i);
                if (e.getNodeName ().equals ("file") || // NOI18N
                    e.getNodeName ().equals ("folder")) { // NOI18N
                    if (e.getAttribute ("name").equals (nextName)) {
                        subel = e;
                        break;
                    }
                }
            }
            return findElementIn (subel, remainder);
        }
    }
    
    public boolean folder (String name) {
        Element el = findElement (name);
        if (el == null) {
            //System.err.log("folder <" + name + ">: false, no such element");
            return false;
        }
        boolean res = el.getNodeName ().equals ("folder"); // NOI18N
        //System.err.log("folder <" + name + ">: " + res);
        return res;
    }
    
    private static final Set warnedAboutDupeKids = new HashSet(1); // Set<String>
    public String[] children (String f) {
        Element el = findElement (f);
        if (el == null) {
            //System.err.log("children <" + f + ">: none, no such element");
            return new String[] {};
        }
        NodeList nl = el.getChildNodes ();
        int len = nl.getLength();
        if (len == 0) {
            //System.err.log("children <" + f + ">: none, no child nodes");
            return new String[] {};
        }
        ArrayList kids = new ArrayList(len); // List<String>
        Set allNames = new HashSet(len); // Set<String>
        for (int i = 0; i < len; i++) {
            Node n = nl.item (i);
            if (n == null) continue; // XXX why does this happen?? does it still?
            if (n.getNodeType () != Node.ELEMENT_NODE) continue;
            Element sub = (Element) n;
            if (sub.getNodeName ().equals ("file") || // NOI18N
                    sub.getNodeName ().equals ("folder")) { // NOI18N
                String name = sub.getAttribute("name"); // NOI18N
                if (allNames.add(name)) {
                    kids.add(name);
                } else {
                    if (warnedAboutDupeKids.add(this + ":" + f + "/" + name)) { // NOI18N
                        // #18699: will deadlock if you try to change anything.
                        if (f.equals("")) { // NOI18N
                            err.log("WARNING: in " + this + " the root folder contains the child " + name + " more than once.");
                        } else {
                            err.log("WARNING: in " + this + " the folder " + f + " contains the child " + name + " more than once.");
                        }
                        err.log("The Open APIs Support module will not work properly with such a layer.");
                        err.log("Please edit the XML text and merge together all children of a <folder> with the same name.");
                    }
                }
            }
        }
        //System.err.log("children <" + f + ">: " + kids);
        return (String[]) kids.toArray (new String[kids.size ()]);
    }
    private int tr(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        return -1;
    }
    
    /** 
     * Converts array of chars to array of bytes. All whitespaces and
     * unknown chars are skipped.
     */
    private void chars2Bytes(OutputStream os, char[] chars, int off, int length)
    throws IOException {
        byte rbyte;
        int read;
        
        for (int i = off; i < length; ) {
            read = tr(chars[i++]);
            if (read >= 0) rbyte = (byte) (read << 4); // * 16;
            else continue;
            
            while (i < length) {
                read = tr(chars[i++]);
                if (read >= 0) {
                    rbyte += (byte) read;
                    os.write(rbyte);
                    break;
                }
            }
        }
    }
    
    /** retrieve byte contents of a named resource */
    private byte[] getContentsOf (final String name) throws FileNotFoundException {
        Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        
        String binaryAttr = (String)readAttribute (name, BINARY_ATTR_NAME);
        if ("true".equals(binaryAttr)) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                NodeList nl = el.getChildNodes ();
                for (int i = 0; i < nl.getLength (); i++) {
                    if (nl.item (i).getNodeType () == Node.TEXT_NODE) {
                        String s = nl.item (i).getNodeValue ().trim ();
                        chars2Bytes(baos, s.toCharArray(), 0, s.length());
                    }
                }
                return baos.toByteArray();
            } catch (IOException ioe) {
                throw new FileNotFoundException (ioe.getMessage ());
            }
        }
        String url = el.getAttribute ("url"); // NOI18N
        if (url.length () > 0) {
            try {
                //  WARNING: this URL used to be relative with respect to the XML file location
                URL u = new URL(url);
                URLConnection conn = u.openConnection ();
                conn.connect ();
                InputStream is = conn.getInputStream ();
                byte[] buf = new byte[conn.getContentLength ()];
                if (is.read (buf) != buf.length) throw new IOException ("wrong content length");
                // Also listen to changes in it.
                FileObject fo = decodeURL (u);
                if (fo != null) {
                    fo.removeFileChangeListener (fileChangeListener);
                    fo.addFileChangeListener (fileChangeListener);
                }
                return buf;
            } catch (IOException ioe) {
                throw new FileNotFoundException (ioe.getMessage ());
            }
        } else {
            StringBuffer buf = new StringBuffer ();
            NodeList nl = el.getChildNodes ();
            for (int i = 0; i < nl.getLength (); i++) {
                if (nl.item (i).getNodeType () == Node.CDATA_SECTION_NODE) {
                    buf.append (nl.item (i).getNodeValue ());
                } else if (nl.item (i).getNodeType () == Node.TEXT_NODE) {
                    buf.append (nl.item (i).getNodeValue ().trim ());
                }
            }
            try {
                // This encoding is intentional...
                return buf.toString ().getBytes ("ISO8859_1"); // NOI18N
            } catch (UnsupportedEncodingException uee) {
                throw new FileNotFoundException (uee.getMessage ());
            }
        }
    }
    
    public InputStream inputStream (String name) throws FileNotFoundException {
        return new ByteArrayInputStream (getContentsOf (name));
    }
    
    public OutputStream outputStream (final String name) throws IOException {
        final Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        // We will change the layer file.
        return new ByteArrayOutputStream () {
            public void close () throws IOException {
                super.close ();
                // If binary data is being stored, convert it to text. For text data,
                // just stick in a CDATA section. Note that by "text" is meant ASCII!
                byte[] contents = toByteArray ();
                char[] contents2 = new char[contents.length];
                boolean binary = false;
                for (int i = 0; i < contents.length; i++) {
                    char c = (char) contents[i];
                    // Note that we assume \r still means it is a text file... the
                    // line ending will not be preserved in XML however! In principle
                    // a very unlucky binary file consisting only of text with some \r
                    // bytes in it, could be mangled. Oh well.
                    if ((c >= ' ' && c <= '~') || c == '\n' || c == '\t' || c == '\r') {
                        contents2[i] = c;
                    } else {
                        binary = true;
                        break;
                    }
                }
                NodeList nl = el.getChildNodes ();
                ArrayList allCdata = new ArrayList (1); // List<CDATASection>
                for (int i = 0; i < nl.getLength (); i++) {
                    if (nl.item (i).getNodeType () == Node.CDATA_SECTION_NODE) {
                        allCdata.add (nl.item (i));
                    } else if (nl.item (i).getNodeType () == Node.TEXT_NODE &&
                               nl.item (i).getNodeValue ().trim ().length () > 0) {
                        el.removeChild (nl.item (i));
                    }
                }
                // XXX if contents2.length == 0, simply remove everything...
                if (! binary) {
                    Node newcontents = doc.createCDATASection (new String (contents2));
                    el.removeAttribute ("url"); // NOI18N
                    Iterator it = allCdata.iterator ();
                    if (it.hasNext ()) {
                        el.replaceChild (newcontents, (CDATASection) it.next ());
                        while (it.hasNext ()) {
                            el.removeChild ((CDATASection) it.next ());
                        }
                    } else {
                        appendWithIndent (el, newcontents);
                    }
                } else {
                    writeAttribute (name, BINARY_ATTR_NAME, "true");
                    Iterator it = allCdata.iterator ();
                    while (it.hasNext ()) {
                        el.removeChild ((CDATASection) it.next ());
                    }
                    StringWriter swrt = new StringWriter();
                    storeData(contents, new PrintWriter(swrt));
                    Node newcontents = doc.createTextNode(swrt.toString());
                    appendWithIndent(el, newcontents);
                }
            }
        };
    }
    
    private void createFileOrFolder (String name, boolean folder) throws IOException {
        if (findElement(name) != null) {
            throw new IOException("File or folder already exists");
        }
        String parentName, baseName;
        int idx = name.lastIndexOf ('/');
        if (idx == -1) {
            parentName = ""; // NOI18N
            baseName = name;
        } else {
            parentName = name.substring (0, idx);
            baseName = name.substring (idx + 1);
        }
        Element el = findElement (parentName);
        if (el == null) throw new FileNotFoundException (parentName);
        Element nue = doc.createElement (folder ? "folder" : "file"); // NOI18N
        nue.setAttribute ("name", baseName); // NOI18N
        appendWithIndent (el, nue);
    }
    
    public void createFolder (String name) throws IOException {
        createFileOrFolder (name, true);
    }
    
    public void createData (String name) throws IOException {
        createFileOrFolder (name, false);
    }
    
    public void delete (String name) throws IOException {
        Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        Node parent = el.getParentNode ();
        if (parent == null) throw new IOException ();
        parent.removeChild (el);
    }
    
    public void rename (String oldName, String newName) throws IOException {
        Element el = findElement (oldName);
        if (el == null) throw new FileNotFoundException (oldName);
        int idx = newName.lastIndexOf ('/');
        if (idx != -1) newName = newName.substring (idx + 1);
        el.setAttribute ("name", newName); // NOI18N
    }
    
    public boolean copy (String name, Transfer target, String targetName) throws IOException {
        if (! (target instanceof XMLBufferFileSystem)) return false;
        XMLBufferFileSystem otherfs = (XMLBufferFileSystem) target;
        if (otherfs.findElement(targetName) != null) {
            throw new IOException("File already exists");
        }
        Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        Element el2;
        if (otherfs == this) {
            el2 = (Element) el.cloneNode (true);
        } else {
            el2 = (Element) otherfs.doc.importNode (el, true);
        }
        String path, base;
        int idx = targetName.lastIndexOf ('/');
        if (idx == -1) {
            path = ""; // NOI18N
            base = targetName;
        } else {
            path = targetName.substring (0, idx);
            base = targetName.substring (idx + 1);
        }
        Element parent = otherfs.findElement (path);
        if (parent == null) throw new FileNotFoundException (path);
        el2.setAttribute ("name", base); // NOI18N
        Element old = otherfs.findElement (targetName);
        if (old != null) {
            parent.replaceChild (el2, old);
        } else {
            appendWithIndent (parent, el2);
        }
        return true;
    }
    
    public boolean move (String name, Transfer target, String targetName) throws IOException {
        if (! (target instanceof XMLBufferFileSystem)) return false;
        XMLBufferFileSystem otherfs = (XMLBufferFileSystem) target;
        if (otherfs.findElement(targetName) != null) {
            throw new IOException("File already exists");
        }
        Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        Element el2;
        if (otherfs == this) {
            // Just move it, no need to clone.
            el2 = el;
        } else {
            el2 = (Element) otherfs.doc.importNode (el, true);
        }
        String path, base;
        int idx = targetName.lastIndexOf ('/');
        if (idx == -1) {
            path = ""; // NOI18N
            base = targetName;
        } else {
            path = targetName.substring (0, idx);
            base = targetName.substring (idx + 1);
        }
        Element parent = otherfs.findElement (path);
        if (parent == null) throw new FileNotFoundException (path);
        el2.setAttribute ("name", base); // NOI18N
        Element old = otherfs.findElement (targetName);
        if (el != el2) {
            // Cross-document import, so need to remove old one.
            el.getParentNode ().removeChild (el);
        }
        if (old != null) {
            parent.replaceChild (el2, old);
        } else {
            appendWithIndent (parent, el2);
        }
        return true;
    }
    
    public Enumeration attributes (String name) {
        Element el = findElement (name);
        if (el == null) return EmptyEnumeration.EMPTY;
        NodeList nl = el.getChildNodes ();
        ArrayList l = new ArrayList (10); // List<String>
        for (int i = 0; i < nl.getLength (); i++) {
            if (nl.item (i).getNodeType () != Node.ELEMENT_NODE) continue;
            Element sub = (Element) nl.item (i);
            if (sub.getNodeName ().equals ("attr")) { // NOI18N
                String myName = sub.getAttribute ("name");
                if (! BINARY_ATTR_NAME.equals(myName)) {
                    // use the binary attribute only internally!
                    l.add (myName); // NOI18N
                }
            }
        }
        return Collections.enumeration (l);
    }
    
    public Object readAttribute (String name, String attrName) {
        Element el = findElement (name);
        if (el == null) return null;
        NodeList nl = el.getChildNodes ();
        for (int i = 0; i < nl.getLength (); i++) {
            if (nl.item (i).getNodeType () != Node.ELEMENT_NODE) continue;
            Element sub = (Element) nl.item (i);
            if (sub.getNodeName ().equals ("attr")) { // NOI18N
                if (sub.getAttribute ("name").equals (attrName)) { // NOI18N
                    org.w3c.dom.Attr v;
                    try {
                        if ((v = sub.getAttributeNode ("bytevalue")) != null) { // NOI18N
                            return new Byte (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("shortvalue")) != null) { // NOI18N
                            return new Short (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("intvalue")) != null) { // NOI18N
                            return new Integer (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("longvalue")) != null) { // NOI18N
                            return new Long (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("floatvalue")) != null) { // NOI18N
                            return new Float (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("doublevalue")) != null) { // NOI18N
                            // When was the last time you set a file attribute to a double?!
                            // Useless list of primitives...
                            return new Double (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("boolvalue")) != null) { // NOI18N
                            return Boolean.valueOf(v.getValue ());
                        } else if ((v = sub.getAttributeNode ("charvalue")) != null) { // NOI18N
                            return new Character (v.getValue ().charAt (0));
                        } else if ((v = sub.getAttributeNode ("stringvalue")) != null) { // NOI18N
                            // Stolen from XMLMapAttr:
                            String inStr = v.getValue ();
                            StringBuffer outStr = new StringBuffer (inStr.length());
                            for (int j = 0; j < inStr.length(); j++) {
                                char ch = inStr.charAt(j);
                                if ( (j+5) <   inStr.length() && ch == '\\' && inStr.charAt(j+1) == 'u' && Character.isDigit(inStr.charAt(j+2))) {
                                    String decChar = inStr.substring(j+2,j+6);
                                    outStr.append((char) Integer.parseInt(decChar,16));
                                    j += 5;
                                }else outStr.append(ch);
                            }
                            return outStr.toString();
                        } else if ((v = sub.getAttributeNode ("methodvalue")) != null) { // NOI18N
                            String value = v.getValue ();
                            Object[] params = new Object[] { findResource (name), attrName };
                            // Stolen from XMLMapAttr:
                            String className,methodName;
                            int j = value.lastIndexOf('.');
                            if (j != -1) {

                                methodName = value.substring(j+1);

                                className = value.substring(0,j);
                                Class cls = Class.forName(className, true, classLoader);

                                Object objArray[][] = {null,null,null};
                                Method methArray[] = {null,null,null};


                                Class fParam = null, sParam = null;

                                if (params != null) {
                                    if (params.length > 0) fParam = params[0].getClass();
                                    if (params.length > 1) sParam = params[1].getClass();
                                }

                                Method[] allMethods = cls.getDeclaredMethods();
                                Class[] paramClss;

                                for (int k=0; k < allMethods.length; k++) {

                                    if (!allMethods[k].getName().equals(methodName))  continue;


                                    paramClss = allMethods[k].getParameterTypes();

                                    if (params == null  || params.length == 0 || paramClss.length == 0) {
                                        if (paramClss.length == 0 && methArray[0] == null && objArray[0] == null) {
                                            methArray[paramClss.length] = allMethods[k];
                                            objArray[paramClss.length] = new Object[] {};
                                            continue;
                                        }

                                        continue;
                                    }


                                    if (paramClss.length == 2 && params.length >= 2  && methArray[2] == null && objArray[2] == null)  {
                                        if (paramClss[0].isAssignableFrom(fParam) && paramClss[1].isAssignableFrom(sParam)) {
                                            methArray[paramClss.length] = allMethods[k];
                                            objArray[paramClss.length] = new Object[] {params[0],params[1]};
                                            break;
                                        }

                                        if (paramClss[0].isAssignableFrom(sParam) && paramClss[1].isAssignableFrom(fParam)) {
                                            methArray[paramClss.length] = allMethods[k];
                                            objArray[paramClss.length] = new Object[] {params[1],params[0]};
                                            break;
                                        }

                                        continue;
                                    }

                                    if (paramClss.length == 1 && params.length >= 1 && methArray[1] == null && objArray[1] == null)  {
                                        if (paramClss[0].isAssignableFrom(fParam)) {
                                            methArray[paramClss.length] = allMethods[k];
                                            objArray[paramClss.length] = new Object[] {params[0]};
                                            continue;
                                        }

                                        if (paramClss[0].isAssignableFrom(sParam)) {
                                            methArray[paramClss.length] = allMethods[k];
                                            objArray[paramClss.length] = new Object[] {params[1]};
                                            continue;
                                        }

                                        continue;
                                    }

                                }

                                for (int l = methArray.length-1; l >= 0; l-- ) {//clsArray.length
                                    if (methArray[l] != null && objArray[l] != null)  {
                                        //Method meth = cls.getDeclaredMethod(methodName,clsArray[l]);
                                        methArray[l].setAccessible(true); //otherwise cannot invoke private
                                        return methArray[l].invoke(null,objArray[l]);
                                    }
                                }
                            }
                            // Some message to logFile
                            throw new InstantiationException (value);
                        } else if ((v = sub.getAttributeNode ("serialvalue")) != null) { // NOI18N
                            // Copied from XMLMapAttr:
                            String value = v.getValue ();
                            if (value.length() == 0) return null;

                            byte[] bytes = new byte[value.length()/2];
                            int tempJ;
                            int count = 0;
                            for (int j = 0; j < value.length(); j += 2) {
                                tempJ = Integer.parseInt(value.substring(j,j+2),16);
                                if (tempJ > 127) tempJ -=256;
                                bytes[count++] = (byte) tempJ;
                            }

                            ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, count);
                            ObjectInputStream ois = new NbObjectInputStream(bis);
                            return ois.readObject();
                        } else if ((v = sub.getAttributeNode ("urlvalue")) != null) { // NOI18N
                            return new URL (v.getValue ());
                        } else if ((v = sub.getAttributeNode ("newvalue")) != null) { // NOI18N
                            return Class.forName(v.getValue(), true, classLoader).newInstance();
                        }
                    } catch (Exception e) {
                        // MalformedURLException, NumberFormatException, reflection stuff, ...
                        err.notify(ErrorManager.INFORMATIONAL, e);
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    private final Set orderAbsorbers = new HashSet (); // Set<String>
    public void writeAttribute (String name, String attrName, Object v) throws IOException {
        if (v == null) {
            String mebbeOrder = name + "/" + attrName; // NOI18N
            if (orderAbsorbers.contains (mebbeOrder)) {
                orderAbsorbers.remove (mebbeOrder);
                return; // see below
            }
        }
        if (attrName.equals ("OpenIDE-Folder-Order") && (v instanceof String)) { // NOI18N
            // This is a special case. We do not want to store a fully fixed order in a layer.
            // Rather, compute some reasonable orderings from it.
            StringTokenizer tok = new StringTokenizer ((String) v, "/"); // NOI18N
            if (tok.hasMoreTokens ()) {
                String prev = tok.nextToken ();
                while (tok.hasMoreTokens ()) {
                    String next = tok.nextToken ();
                    writeAttribute (name, prev + "/" + next, Boolean.TRUE); // NOI18N
                    // DataFolder tries to cancel these orders immediately after writing!
                    // Don't let it.
                    orderAbsorbers.add (name + "/" + prev + "/" + next); // NOI18N
                    prev = next;
                }
            }
            return;
        }
        Element el = findElement (name);
        if (el == null) throw new FileNotFoundException (name);
        Element attr = null;
        NodeList nl = el.getChildNodes ();
        for (int i = 0; i < nl.getLength (); i++) {
            if (nl.item (i).getNodeType () != Node.ELEMENT_NODE) continue;
            Element e = (Element) nl.item (i);
            if (e.getNodeName ().equals ("attr") && e.getAttribute ("name").equals (attrName)) { // NOI18N
                attr = e;
                break;
            }
        }
        if (v == null) {
            if (attr != null) {
                el.removeChild (attr);
            }
            Comment c = findSerialComment (el, attrName);
            if (c != null) el.removeChild (c);
            return;
        }
        boolean adding;
        if (attr == null) {
            attr = doc.createElement ("attr"); // NOI18N
            attr.setAttribute ("name", attrName); // NOI18N
            adding = true;
        } else {
            // Kill old *value.
            NodeList nl2 = attr.getChildNodes ();
            for (int j = 0; j < nl2.getLength (); j++) {
                Node n = nl2.item (j);
                if (n.getNodeType () == Node.ATTRIBUTE_NODE && ! n.getNodeName ().equals ("name")) { // NOI18N
                    attr.removeChild (n);
                }
            }
            adding = false;
        }
        String serialComment = null;
        if (v instanceof Byte) {
            attr.setAttribute ("bytevalue", v.toString ()); // NOI18N
        } else if (v instanceof Short) {
            attr.setAttribute ("shortvalue", v.toString ()); // NOI18N
        } else if (v instanceof Integer) {
            attr.setAttribute ("intvalue", v.toString ()); // NOI18N
        } else if (v instanceof Long) {
            attr.setAttribute ("longvalue", v.toString ()); // NOI18N
        } else if (v instanceof Float) {
            attr.setAttribute ("floatvalue", v.toString ()); // NOI18N
        } else if (v instanceof Double) {
            attr.setAttribute ("doublevalue", v.toString ()); // NOI18N
        } else if (v instanceof Character) {
            attr.setAttribute ("charvalue", v.toString ()); // NOI18N
        } else if (v instanceof Boolean) {
            attr.setAttribute ("boolvalue", v.toString ()); // NOI18N
        } else if (v instanceof String) {
            String inStr = (String) v;
            // Stolen from XMLMapAttr:
            StringBuffer   outStr = new StringBuffer (6*inStr.length());
            boolean isConv;
            char[] toReplace = {'&','<','>','\"','\''};

            for (int i = 0; i < inStr.length(); i++) {
                isConv = false;
                for (int j = 0; j < toReplace.length; j++) {                
                    if (inStr.charAt(i) == toReplace[j] ) {                                        
                        outStr.append(encodeChar (inStr.charAt(i)));
                        isConv = true; break;
                    }
                }
                if (!isConv) {
                    if (Character.isISOControl(inStr.charAt(i))) {
                        outStr.append(encodeChar (inStr.charAt(i)));
                        continue;
                    }
                }
                if (!isConv) outStr.append(inStr.charAt(i));
            }
            attr.setAttribute ("stringvalue", outStr.toString ()); // NOI18N
        } else if (v instanceof URL) {
            attr.setAttribute ("urlvalue", v.toString ()); // NOI18N
        } else {
            // Stolen from XMLMapAttr, mostly.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(v);
            oos.close();
            byte bArray[] = bos.toByteArray();
            // Check to see if this is the same as a default instance.
            Class clazz = v.getClass ();
            boolean usenewinstance = false;
            try {
                Object v2 = clazz.newInstance ();
                bos = new ByteArrayOutputStream ();
                oos = new ObjectOutputStream (bos);
                oos.writeObject (v2);
                oos.close ();
                byte[] bArray2 = bos.toByteArray ();
                usenewinstance = Utilities.compareObjects (bArray, bArray2);
            } catch (Exception e) {
                // quite expectable - ignore
            }
            if (usenewinstance) {
                attr.setAttribute ("newvalue", clazz.getName ()); // NOI18N
            } else {
                StringBuffer strBuff = new StringBuffer(bArray.length*2);
                for(int i = 0; i < bArray.length;i++) {
                    if (bArray[i] < 16 && bArray[i] >= 0) strBuff.append("0");// NOI18N
                    strBuff.append(Integer.toHexString(bArray[i] < 0?bArray[i]+256:bArray[i]));            
                }
                attr.setAttribute ("serialvalue", strBuff.toString()); // NOI18N
                // Also mention what the original value was, for reference.
                // Do it after adding element since otherwise we cannot indent correctly.
                String asString;
                if (clazz.isArray ()) {
                    // Default toString sucks for arrays. Pretty common so worth special-casing.
                    asString = Arrays.asList ((Object[]) v).toString ();
                } else {
                    asString = v.toString ();
                }
                serialComment = " (" + attrName + "=" + asString + ") ";
            }
        }
        if (adding) {
            appendWithIndent (el, attr);
        }
        // Deal with serial comments now.
        Comment comment = findSerialComment (el, attrName);
        if (serialComment != null) {
            if (comment != null) {
                comment.setData (serialComment);
            } else {
                appendWithIndent (el, doc.createComment (serialComment));
            }
        } else if (comment != null) {
            // Changed from some serialvalue to simple value; kill comment.
            el.removeChild (comment);
        }
        if (attrName.startsWith ("SystemFileSystem")) { // NOI18N
            fireFileStatusChanged (new FileStatusEvent (this, findResource (name), true, true));
        }
    }
    private Comment findSerialComment (Element el, String attrName) {
        NodeList nl = el.getChildNodes ();
        for (int i = 0; i < nl.getLength (); i++) {
            if (nl.item (i).getNodeType () == Node.COMMENT_NODE) {
                String comm = nl.item (i).getNodeValue ();
                if (comm.startsWith (" (" + attrName + "=") && comm.endsWith (") ")) {
                    return (Comment) nl.item (i);
                }
            }
        }
        return null;
    }
    
    /** stolen from XMLMapAttr */
    private static String encodeChar (char ch) {
        String encChar= Integer.toString((int)ch,16);        
        return "\\u"+"0000".substring(0,"0000".length()-encChar.length()).concat(encChar); // NOI18N
    }

    public void renameAttributes (String oldName, String newName) {
        // do nothing
    }
    
    public void deleteAttributes (String name) {
        // do nothing
    }
    
    public boolean readOnly (String name) {
        return false;
    }
    
    public String mimeType (String name) {
        return null; // i.e. use default resolvers
    }
    
    public long size (String name) {
        try {
            return getContentsOf (name).length;
        } catch (FileNotFoundException fnfe) {
            return 0;
        }
    }
    
    public void markUnimportant (String name) {
        Element el = findElement (name);
        if (el == null) return;
        String message = " This file is not important. ";
        NodeList nl = el.getChildNodes ();
        for (int i = 0; i < nl.getLength (); i++) {
            if (nl.item (i).getNodeType () == Node.COMMENT_NODE &&
                    nl.item (i).getNodeValue ().equals (message)) {
                // Already have it, leave it there.
                return;
            }
        }
        appendWithIndent (el, doc.createComment (message));
    }
    
    public Date lastModified (String name) {
        // For files taken from an external URL which is a file object, delegate.
        Element el = findElement (name);
        if (el != null) {
            String u = el.getAttribute ("url"); // NOI18N
            if (u.length() > 0) {
                try {
                    // WARNING: this URL used to be relative with respect to the XML file
                    FileObject fo = decodeURL(new URL(u));
                    if (fo != null) {
                        fo.removeFileChangeListener (fileChangeListener);
                        fo.addFileChangeListener (fileChangeListener);
                        Date d = fo.lastModified ();
                        //System.err.log("lastModified <" + name + ">: " + d.getTime());
                        return d;
                    }
                } catch (MalformedURLException mfue) {
                    err.notify(ErrorManager.INFORMATIONAL, mfue);
                    // ignore
                }
            }
        }
        // Return last mod of document, since that will trigger content
        // refreshes of files where necessary.
        //System.err.log("lastModified <" + name + ">: [no URL] " + time.getTime());
        return time;
    }
    
    // These are not important for us:
    
    public void lock (String name) throws IOException {
        // [PENDING] should this try to lock the XML document??
        // (not clear if it is safe to do so from FS call, even tho
        // on a different FS)
    }
    
    public void unlock (String name) {
        // do nothing
    }
    
    // XXX ideally would clean up indentation after deleting elements, too....
    
    // don't bother making configurable; in the future an indentation engine will do it...
    private static final int INDENT_STEP = 4;
    /** Stolen from Ant module's ElementNode: appends DOM node with proper indentation. */
    private static void appendWithIndent (Element parent, Node child) throws DOMException {
        Node doc = parent;
        int depth = -1;
        while (! (doc instanceof Document)) {
            doc = doc.getParentNode ();
            depth++;
        }
        String ws1;
        if (parent.hasChildNodes ()) {
            ws1 = spaces (INDENT_STEP);
        } else {
            ws1 = "\n" + spaces ((depth + 1) * INDENT_STEP);
        }
        parent.appendChild (((Document) doc).createTextNode (ws1));
        parent.appendChild (child);
        parent.appendChild (((Document) doc).createTextNode ("\n" + spaces (depth * INDENT_STEP)));
    }
    private static String spaces (int size) {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = ' ';
        }
        return new String (chars);
    }
    
    // Listen to changes in files used as url= external contents; or
    // bundles & icons used to annotate names. If these change,
    // the filesystem needs to show something else. Properly we would
    // keep track of *which* file changed and thus which of our resources
    // is affected. Practically this would be a lot of work and gain
    // very little.
    public void fileDeleted (FileEvent fe) {
        someFileChange (fe);
    }
    public void fileFolderCreated (FileEvent fe) {
        // does not apply to us
    }
    public void fileDataCreated (FileEvent fe) {
        // In case a file was created that makes an annotation be available.
        // We are listening to the parent folder, so if e.g. a new branded variant
        // of a bundle is added, the display ought to be refreshed accordingly.
        someFileChange (fe);
    }
    public void fileAttributeChanged (FileAttributeEvent fe) {
        // don't care about attributes on included files...
    }
    public void fileRenamed (FileRenameEvent fe) {
        someFileChange (fe);
    }
    public void fileChanged (FileEvent fe) {
        someFileChange (fe);
    }
    private void someFileChange (FileEvent fe) {
        final boolean expected = fe.isExpected ();
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                // If used as url=, refresh contents, timestamp, ...
                refreshResource ("", expected); // NOI18N
                // If used as nbres: annotation, fire status change.
                fireFileStatusChanged (new FileStatusEvent (XMLBufferFileSystem.this, true, true));
            }
        });
    }

    private static FileObject decodeURL(URL u) {
        FileObject[] fos = URLMapper.findFileObjects(u);
        return (fos.length > 0) ? fos[0] : null;
    }
    
    public java.awt.Image annotateIcon(java.awt.Image icon, int iconType, java.util.Set files) {
        return icon;
    }
    
    public String annotateName(String name, java.util.Set files) {
        return name;
    }
    
    private static void storeData (byte[] bdata, PrintWriter pw) throws IOException {
        char[] cdata = new char[BUFFSIZE];
        for (int i=0; i < INDENT; i++ ) cdata[i] = ' ';
        
        int i = 0; // byte array pointer
        int j; // char array pointer
        int blen = bdata.length;
        
        while (i < blen) {
            int mark = INDENT + Math.min( 2*(blen-i), BLOCK );
            for (j=INDENT; j < mark; j += 2) {
                int b = ((int)bdata[i++]) + 256;
                cdata[j]   = HEXDIGITS[b >> 4];
                cdata[j+1] = HEXDIGITS[b & 15];
            }
            pw.write(cdata, 0, j);
            pw.println();
        }
        pw.flush();
    }
    
}
