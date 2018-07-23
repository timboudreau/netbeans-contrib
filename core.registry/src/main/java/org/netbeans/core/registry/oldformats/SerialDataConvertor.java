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

package org.netbeans.core.registry.oldformats;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.netbeans.core.registry.DocumentUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 *
 * @author copy&pasted from core/settings
 */

public class SerialDataConvertor {
    
    private static final String INSTANCE_DTD_ID = "-//NetBeans//DTD Session settings 1.0//EN"; // NOI18N
    private static final String INSTANCE_DTD_WWW = "http://www.netbeans.org/dtds/sessionsettings-1_0.dtd"; // NOI18N

    // enlarged to not need do the test for negative byte values
    private final static char[] HEXDIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
                                             '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                                             '0', '1', '2', '3', '4', '5', '6', '7'};
    
    private static final int INDENT = 8;
    private static final int BLOCK = 64;
    private static final int BUFFSIZE = INDENT + BLOCK;

    private String moduleCodeName = null;
    
    public SerialDataConvertor() {
    }

    public static Object read(org.w3c.dom.Element element, FileObject fo) throws IOException, ClassNotFoundException {
        String moduleCodeName = null;
        String instanceClass = null;
        String instanceMethod = null;
        String serialData = null;
        
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (e.getNodeName().equals("module")) {
                    moduleCodeName = e.getAttribute("name");
                }
                if (e.getNodeName().equals("instance")) {
                    instanceClass = e.getAttribute("class");
                    instanceMethod = e.getAttribute("method");
                    if (instanceMethod.length() == 0) {
                        instanceMethod = null;
                    }
                    instanceClass = org.openide.util.Utilities.translate(instanceClass);
                }
                if (e.getNodeName().equals("serialdata")) {
                    instanceClass = e.getAttribute("class");
                    serialData = DocumentUtils.getTextValue(e);
                    instanceClass = org.openide.util.Utilities.translate(instanceClass);
                }
            }
        }
        return createInstance(serialData, instanceClass, instanceMethod, fo);
    }

    public static boolean isSettingsFormat(org.w3c.dom.Element element) {
        if (element.getNodeName().equals("settings") &&
            element.getNamespaceURI() == null &&
            element.getAttribute("version").equals("1.0")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String getModuleCodeName(org.w3c.dom.Element element) {
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (e.getNodeName().equals("module")) {
                    return e.getAttribute("name");
                }
            }
        }
        return null;
    }

    
    public void write(FileObject fo, Object inst) throws IOException, UnsupportedOperationException {
        FileLock lock = fo.lock();
        OutputStream os = fo.getOutputStream(lock);
        boolean ok = false;
        try {
            PrintWriter pw = new PrintWriter (os);
            pw.println("<?xml version=\"1.0\"?>"); // NOI18N
            pw.print("<!DOCTYPE settings PUBLIC \"");
            pw.print(INSTANCE_DTD_ID); // NOI18N
            pw.print("\" \"");
            pw.print(INSTANCE_DTD_WWW);
            pw.println("\">"); // NOI18N
            pw.println("<settings version=\"1.0\">"); // NOI18N

            storeModule(inst, pw);

            storeInstanceOf(inst, pw);

            // TODO: if (inst isBean) then user XMLArchiver instead of Serialization here
            storeSerialData(inst, pw);

            pw.println("</settings>"); // NOI18N
            pw.flush();
            ok = true;
        } finally {
            os.close();
            lock.releaseLock();
            if (!ok) {
                fo.delete();
            }
        }
    }
    
    private static Object createInstance(String serialData, String instanceClass, String instanceMethod, FileObject fo) throws IOException, ClassNotFoundException {
        if (serialData != null) {
            return InstanceUtils.serialValue(serialData);
        }
        if (instanceMethod != null) {
            return InstanceUtils.methodValue(instanceClass, instanceMethod, fo);
        }
        if (instanceClass != null) {
            return InstanceUtils.newValue(instanceClass);
        }
        throw new IOException("Do not know how to create instance from file XXXX");
        
    }
    
    private void storeInstanceOf (Object inst, PrintWriter pw) throws IOException {
        Iterator it = getSuperClasses(inst.getClass(), null).iterator();
        Class clazz;
        while (it.hasNext()) {
            clazz = (Class) it.next();
            pw.print("    <instanceof class=\""); // NOI18N
            pw.print(clazz.getName());
            pw.println("\"/>"); // NOI18N
        }
    }
    
    private void storeModule(Object inst, PrintWriter pw) throws IOException {
        Iterator it = Lookup.getDefault().lookup(
            new Lookup.Template(ModuleInfo.class)).allInstances().iterator();
        while (it.hasNext()) {
            ModuleInfo mi = (ModuleInfo)it.next();
            if (mi.owns(inst.getClass())) {
                String modulName = mi.getCodeName();
                SpecificationVersion spec = mi.getSpecificationVersion();
                pw.print("    <module"); // NOI18N
                if (modulName != null && modulName.length() != 0) {
                    pw.print(" name=\"");
                    pw.print(modulName);
                    pw.print('"');// NOI18N
                }
                if (spec != null) {
                    pw.print(" spec=\"");
                    pw.print(spec.toString());
                    pw.print('"');// NOI18N
                }
                pw.println("/>"); // NOI18N
                
                moduleCodeName = modulName;
                
                return;
            }
        }
        Logger.getLogger(getClass().getName()).log(
                Level.FINE,
                "ModuleInfo was not found for class "+inst.getClass());
    }
    

    // returns module code name of stored instance
    public String getModuleCodeName() {
        return moduleCodeName;
    }
    
    private void storeSerialData (Object inst, PrintWriter pw) throws IOException {
        pw.print ("    <serialdata class=\"");
        pw.print(inst.getClass().getName());
        pw.println("\">"); // NOI18N

        ByteArrayOutputStream baos = new ByteArrayOutputStream (1024);
        ObjectOutput oo = new SpecialObjectOutputStream (baos);
        oo.writeObject (inst);
        byte[] bdata = baos.toByteArray ();
        
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
        pw.println ("    </serialdata>"); // NOI18N
        pw.flush();
    }
    
    /** Get everything what class extends or implements. */
    private Set getSuperClasses(Class clazz, Set classes) {
        if (classes == null) {
            classes = new HashSet();
        }
        
        if (clazz == null || !classes.add(clazz)) {
            return classes;
        }
        
        Class[] cs = clazz.getInterfaces();
        
        // XXX following validation should help to identify a wrong IBM's
        // implementation of Class.getInterfaces(). The implementation for some
        // classes returns null. See issue #16257.
        if (cs != null) {
            for (int i = 0; i < cs.length; i++) {
                getSuperClasses(cs[i], classes);
            }
        } else {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "Error: if you encounter this message, please attach " + //NOI18N
                "the class name to the issue http://www.netbeans.org/issues/show_bug.cgi?id=16257. " + //NOI18N
                "Class.getInterfaces() == null for the class: " + clazz); // NOI18N
        }
        
        return getSuperClasses(clazz.getSuperclass(), classes);
    }

    
    /** The only purpose of this object output stream subclass is to 
     * detect whether the serialized object does not return null
     * in its writeReplace method. Such an object is ignored and exception
     * is thrown. More details in issue #15563.
     */
    private static class SpecialObjectOutputStream extends org.openide.util.io.NbObjectOutputStream {

        private boolean first;
        
        public SpecialObjectOutputStream(OutputStream os) throws IOException {
            super (os);
            first = true;
        }

        public Object replaceObject (Object obj) throws IOException {
            if (first) {
                if (obj == null) {
                    // Object doesn't want to be serialized.
                    throw new NotSerializableException();
                }
                first = false;
            }
            return super.replaceObject(obj);
        }

    }

    private static class SettingsSAXHandler extends DefaultHandler {
        
        private static final String ELM_SETTING = "settings"; // NOI18N
        private static final String ATR_SETTING_VERSION = "version"; // NOI18N
        
        private static final String ELM_MODULE = "module"; // NOI18N
        private static final String ATR_MODULE_NAME = "name"; // NOI18N
        private static final String ATR_MODULE_SPEC = "spec"; // NOI18N
        private static final String ATR_MODULE_IMPL = "impl"; // NOI18N
        
        private static final String ELM_INSTANCE = "instance"; // NOI18N
        private static final String ATR_INSTANCE_CLASS = "class"; // NOI18N
        private static final String ATR_INSTANCE_METHOD = "method"; // NOI18N
        
        private static final String ELM_INSTANCEOF = "instanceof"; // NOI18N
        private static final String ATR_INSTANCEOF_CLASS = "class"; // NOI18N
        
        private static final String ELM_SERIALDATA = "serialdata"; // NOI18N
        private static final String ATR_SERIALDATA_CLASS = "class"; // NOI18N
        
        
        
        // ATR_MODULE_NAME
        private String codeName;
        // ATR_INSTANCE_CLASS
        private String instanceClass;
       
        // ATR_INSTANCEOF_CLASS
        private Set instanceOf = new HashSet();

        // ATR_INSTANCE_METHOD
        private String instanceMethod;


        // ATR_MODULE_SPEC
        private SpecificationVersion moduleSpec;
        // ATR_MODULE_IMPL
        private String moduleImpl;

        // ATR_SETTING_VERSION
        private String version;
        
        
        // ATR_SERIALDATA_CLASS
        private String serialdata;
        
        
        // module base name
        private String codeNameBase;
        
        // module base version
        private int codeNameRelease;

        
        private Stack stack;
        private CharArrayWriter chaos = null;
        
        private boolean readModuleOnly;
        
        public SettingsSAXHandler(boolean readModuleOnly) {
            stack = new Stack();
            this.readModuleOnly = readModuleOnly;
        }
        
        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws SAXException {
            if (INSTANCE_DTD_ID.equals(publicId)) {
                return new org.xml.sax.InputSource(new ByteArrayInputStream(new byte[0]));
            } else {
                return null; // i.e. follow advice of systemID
            }
        }
        
        public void characters(char[] values, int start, int length) throws SAXException {
            String element = (String) stack.peek();
            if (ELM_SERIALDATA.equals(element)) {
                // [PENDING] should be optimized to do not read all chars to memory
                if (chaos == null) {
                    chaos = new CharArrayWriter(length);
                }
                chaos.write(values, start, length);
            }
        }
        
        public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
            stack.push(qName);
            if (ELM_SETTING.equals(qName)) {
                version = attribs.getValue(ATR_SETTING_VERSION);
            } else if (ELM_MODULE.equals(qName)) {
                codeName = attribs.getValue(ATR_MODULE_NAME);
                resolveModuleElm(codeName);
                moduleImpl = attribs.getValue(ATR_MODULE_IMPL);
                if (readModuleOnly) {
                    throw new StopSAXException();
                }
                try {
                    String spec = attribs.getValue(ATR_MODULE_SPEC);
                    moduleSpec = spec == null ? null : new SpecificationVersion(spec);
                } catch (NumberFormatException nfe) {
                    throw new SAXException(nfe);
                }
            } else if (ELM_INSTANCEOF.equals(qName)) {
                instanceOf.add(org.openide.util.Utilities.translate(attribs.getValue(ATR_INSTANCEOF_CLASS)));
            } else if (ELM_INSTANCE.equals(qName)) {
                instanceClass = attribs.getValue(ATR_INSTANCE_CLASS);
                instanceClass = org.openide.util.Utilities.translate(instanceClass);
                instanceMethod = attribs.getValue(ATR_INSTANCE_METHOD);
            } else if (ELM_SERIALDATA.equals(qName)) {
                instanceClass = attribs.getValue(ATR_SERIALDATA_CLASS);
                instanceClass = org.openide.util.Utilities.translate(instanceClass);
            }
        }
        
        /** reade codenamebase + revision */
        private void resolveModuleElm(String codeName) {
            if (codeName != null) {
                int slash = codeName.indexOf("/"); // NOI18N
                if (slash == -1) {
                    codeNameBase = codeName;
                    codeNameRelease = -1;
                } else {
                    codeNameBase = codeName.substring(0, slash);
                    try {
                        codeNameRelease = Integer.parseInt(codeName.substring(slash + 1));
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(getClass().getName()).log(
                            Level.FINE, codeName, ex);

                        codeNameRelease = -1;
                    }
                }
            } else {
                codeNameBase = null;
                codeNameRelease = -1;
            }
        }
        
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String element = (String) stack.pop();
            if (ELM_SERIALDATA.equals(element)) {
                if (chaos != null) {
                    serialdata = chaos.toString();
                }
            }
        }
        
    }
    
    final static class StopSAXException extends SAXException {
        public StopSAXException() {
            super("Parser stopped"); // NOI18N
        }
    }
    
}
