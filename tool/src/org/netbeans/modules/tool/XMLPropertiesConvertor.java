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
 * Software is Sun Microsystems, Inc. Portions Copyright 2002-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tool;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;

import org.netbeans.modules.settings.Env;
import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.Saver;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

/**
 * Copied from nb_all/core/settings/src/org/netbeans/modules/settings/convertors/XMLPropertiesConvertor.java
*
 * Implementation of xml properties format described by
 * /org/netbeans/modules/settings/resources/properties.dtd
 *
 * @author  Jan Pokorsky, adapted by David Strupl
 */
public final class XMLPropertiesConvertor extends Convertor implements PropertyChangeListener {
    /** file attribute containnig value whether the setting object will be
     * stored automaticaly (preventStoring==false) or SaveCookie will be provided.
     * Default value is <code>preventStoring==false</code>. Usage
     * <code>&lt;attr name="xmlproperties.preventStoring" boolvalue="[true|false]"/>
     * </code>
     */
    public final static String EA_PREVENT_STORING = "xmlproperties.preventStoring"; //$NON-NLS-1$
    /** file attribute containnig list of property names their changes will be ignored. Usage
     * <code>&lt;attr name="xmlproperties.ignoreChanges" stringvalue="name[, ...]"/>
     * </code>
     */
    public final static String EA_IGNORE_CHANGES = "xmlproperties.ignoreChanges"; //$NON-NLS-1$
    
    private static final String PROP_CLASS_NAME = "xmlproperties.className"; //$NON-NLS-1$
    
    private FileObject providerFO;
    /** cached property names to be filtered */
    private java.util.Set ignoreProperites;
    
    /** create convertor instance; should be used in module layers
     * @param providerFO provider file object
     */
    public static Convertor create(org.openide.filesystems.FileObject providerFO) {
        return new XMLPropertiesConvertor(providerFO);
    }
    
    public XMLPropertiesConvertor(org.openide.filesystems.FileObject fo) {
        this.providerFO = fo;
    }
    
    public Object read(java.io.Reader r) throws IOException, ClassNotFoundException {
        Properties p = readSetting(r);
        String className = p.getProperty(PROP_CLASS_NAME);
        if (className == null) {
            Object[] args = { "xmlproperties.className", p }; //$NON-NLS-1$
            String message = NbBundle.getMessage( getClass( ), "XMLProprtiesConvertor.MissingKey", args ); //$NON-NLS-1$
            throw new IOException(message);
        }
        Object def = defaultInstanceCreate(className);
        invokeReadProperties(p, def);
        return def;
    }
    
    public void write(java.io.Writer w, Object inst) throws IOException {
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"); //$NON-NLS-1$
        w.write("<!DOCTYPE properties PUBLIC \""); //$NON-NLS-1$
        
        FileObject foEntity = Env.findEntityRegistration(providerFO);
        if (foEntity == null) foEntity = providerFO;
        Object publicId = foEntity.getAttribute(Env.EA_PUBLICID);
        if (publicId == null || !(publicId instanceof String)) {
            throw new IOException("missing or invalid attribute: " + //$NON-NLS-1$
                Env.EA_PUBLICID + ", provider: " + foEntity); //$NON-NLS-1$
        }
        
        w.write((String) publicId);
        w.write("\" \"http://www.netbeans.org/dtds/properties-1_0.dtd\">\n"); //$NON-NLS-1$
        w.write("<properties>\n"); //$NON-NLS-1$
        Properties p = getProperties(inst);
        p.setProperty(PROP_CLASS_NAME, inst.getClass().getName());
        if (p != null && !p.isEmpty()) writeProperties(w, p);
        w.write("</properties>\n"); //$NON-NLS-1$
    }
    
    /** an object listening on the setting changes */
    private Saver saver;
    public void registerSaver(Object inst, Saver s) {
        if (saver != null) {
            String message = NbBundle.getMessage(getClass(),"XMLProprtiesConvertor.AlreadyRegistered"); //$NON-NLS-1$
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, message); 
            return;
        }
        
        // add propertyChangeListener
        try {
            java.lang.reflect.Method method = inst.getClass().getMethod(
                "addPropertyChangeListener", //$NON-NLS-1$
                new Class[] {PropertyChangeListener.class});
            method.invoke(inst, new Object[] {this});
            this.saver = s;
//System.out.println("XMLPropertiesConvertor.registerPropertyListener...ok " + inst);
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL,
            "ObjectChangesNotifier: NoSuchMethodException: " + //$NON-NLS-1$
            inst.getClass().getName() + ".addPropertyChangeListener"); //$NON-NLS-1$
        } catch (IllegalAccessException ex) {
            ErrorManager.getDefault().notify(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public void unregisterSaver(Object inst, Saver s) {
        if (saver == null) return;
        if (saver != s) {
            String message = NbBundle.getMessage(getClass(), "XMLProprtiesConvertor.CannotUnregister"); //$NON-NLS-1$
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, message);
            return;
        }
        try {
            java.lang.reflect.Method method = inst.getClass().getMethod(
                "removePropertyChangeListener", //$NON-NLS-1$
                new Class[] {PropertyChangeListener.class});
            method.invoke(inst, new Object[] {this});
            this.saver = null;
//System.out.println("XMLPropertiesConvertor.unregisterPropertyListener...ok " + inst);
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL,
            "ObjectChangesNotifier: NoSuchMethodException: " + //$NON-NLS-1$
            inst.getClass().getName() + ".removePropertyChangeListener"); //$NON-NLS-1$
            // just changes done through gui will be saved
        } catch (IllegalAccessException ex) {
            ErrorManager.getDefault().notify(ex);
            // just changes done through gui will be saved
        } catch (java.lang.reflect.InvocationTargetException ex) {
            ErrorManager.getDefault().notify(ex);
            // just changes done through gui will be saved
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (saver == null || ignoreChange(evt)) return;
        if (acceptSave()) {
            try {
                saver.requestSave();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }
        } else {
            saver.markDirty();
        }
    }
    
    
    ////////////////////////////////////////////////////////////
    // Private implementation
    ////////////////////////////////////////////////////////////
    
    /** filtering of Property Change Events */
    private boolean ignoreChange(java.beans.PropertyChangeEvent pce) {
        if (pce == null || pce.getPropertyName() == null) return true;
        
        if (ignoreProperites == null) {
            ignoreProperites = Env.parseAttribute(
                providerFO.getAttribute(EA_IGNORE_CHANGES));
        }
        if (ignoreProperites.contains(pce.getPropertyName())) return true;
        
        return ignoreProperites.contains("all"); //$NON-NLS-1$
    }
    
    private boolean acceptSave() {
        Object storing = providerFO.getAttribute(EA_PREVENT_STORING);
        if (storing == null) return true;
        if (storing instanceof Boolean)
            return !((Boolean) storing).booleanValue();
        if (storing instanceof String)
            return !Boolean.valueOf((String) storing).booleanValue();
        return true;
    }
    
    private final static String INDENT = "    "; //$NON-NLS-1$
    private String instanceClass = null;
    

    private Object defaultInstanceCreate(String className) throws IOException, ClassNotFoundException {
        Object instanceCreate = providerFO.getAttribute(Env.EA_INSTANCE_CREATE);
        if (instanceCreate != null) return instanceCreate;
        
        Class c = getInstanceClass(className);
        try {
            return c.newInstance();
        } catch (Exception ex) { // IllegalAccessException, InstantiationException
            Object[] args = { c.getName() };
            String message = NbBundle.getMessage(getClass(), "XMLProprtiesConvertor.CannotInstantiate", args); //$NON-NLS-1$
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(message),
                ex);
        }
    }

    private Class getInstanceClass(String className) throws IOException, ClassNotFoundException {
        if (instanceClass == null) {
            instanceClass = className;
        }
        return ((ClassLoader)Lookup.getDefault().lookup(ClassLoader.class)).loadClass(instanceClass);
    }
    
    private Properties readSetting(java.io.Reader input) throws IOException {
        XMLPropertiesConvertor.Reader r = new XMLPropertiesConvertor.Reader();
        r.parse(input);
        return r.getProperties();
    }
    
    private void invokeReadProperties(Properties p, Object inst) throws IOException {
        try {
            java.lang.reflect.Method m = inst.getClass().getMethod(
                "readProperties", new Class[] {Properties.class}); //$NON-NLS-1$
            m.invoke(inst, new Object[] { p });
        } catch (NoSuchMethodException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (IllegalAccessException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(t.getLocalizedMessage()), t);
        }
    }    
    
    private static void writeProperties(java.io.Writer w, Properties p) throws IOException {
        java.util.Iterator it = p.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            w.write(INDENT);
            w.write("<property name=\""); //$NON-NLS-1$
            w.write(key);
            w.write("\" value=\""); //$NON-NLS-1$
            String val = p.getProperty(key);
            w.write(escapeXmlContent(val));
            w.write("\"/>\n"); //$NON-NLS-1$
        }
    }

    private static Properties getProperties (Object inst) throws IOException {
        try {
            java.lang.reflect.Method m = inst.getClass().getMethod(
                "writeProperties", new Class[] {Properties.class}); //$NON-NLS-1$
            m.setAccessible(true);
            Properties prop = new Properties();
            m.invoke(inst, new Object[] {prop});
            return prop;
        } catch (NoSuchMethodException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (IllegalAccessException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(t.getLocalizedMessage()), t);
        }
    }
    
    /** 
     * Mantis 184 - handle chars like & and ' in names of tools
     */
    private static String escapeXmlContent (String text) {
        if (text.indexOf('&') != -1 ||
            text.indexOf('\'') != -1 ||
            text.indexOf('<') != -1 ||
            text.indexOf('>') != -1 ||
            text.indexOf('\"') != -1    
                ) {
            text = text.replaceAll("&", "&amp;");
            text = text.replaceAll("'", "&apos;");
            text = text.replaceAll("\"", "&quot;");
            text = text.replaceAll("<", "&lt;");
            text = text.replaceAll(">", "&gt;");
        }
        return text;
    }

    
    /** support for reading xml/properties format */
    private static class Reader extends org.xml.sax.helpers.DefaultHandler implements org.xml.sax.ext.LexicalHandler {
        Reader() {}

        private static final String ELM_PROPERTY = "property"; //$NON-NLS-1$
        private static final String ATR_PROPERTY_NAME = "name"; //$NON-NLS-1$
        private static final String ATR_PROPERTY_VALUE = "value"; //$NON-NLS-1$

        private Properties props = new Properties();
        private String publicId;

        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
            if (this.publicId != null && this.publicId.equals (publicId)) {
                return new org.xml.sax.InputSource (new java.io.ByteArrayInputStream (new byte[0]));
            } else {
                return null; // i.e. follow advice of systemID
            }
        }

        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attribs) throws SAXException {
            if (ELM_PROPERTY.equals(qName)) {
                String propertyName = attribs.getValue(ATR_PROPERTY_NAME);
                String propertyValue = attribs.getValue(ATR_PROPERTY_VALUE);
                props.setProperty(propertyName, propertyValue);
            }
        }

        public void parse(java.io.Reader src) throws IOException {
            try {
                org.xml.sax.XMLReader reader = org.openide.xml.XMLUtil.createXMLReader(false, false);
                reader.setContentHandler(this);
                reader.setEntityResolver(this);
                org.xml.sax.InputSource is =
                    new org.xml.sax.InputSource(src);
                try {
                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);  //$NON-NLS-1$
                } catch (SAXException sex) {
                    ErrorManager.getDefault().log(ErrorManager.EXCEPTION,
                    NbBundle.getMessage(getClass(), "XMLProprtiesConvertor.BadParser")); //$NON-NLS-1$
                }
                reader.parse(is);
            } catch (SAXException ex) {
                IOException ioe = new IOException();
                ErrorManager emgr = ErrorManager.getDefault();
                emgr.annotate(ioe, ex);
                if (ex.getException () != null) {
                    emgr.annotate (ioe, ex.getException());
                }
                throw ioe;
            }
        }
        
        public Properties getProperties() {
            return props;
        }
        
        public String getPublicID() {
            return publicId;
        }

        // LexicalHandler implementation
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this.publicId = publicId;
        }
        
        public void endDTD() throws SAXException {}
        public void startEntity(String str) throws SAXException {}
        public void endEntity(String str) throws SAXException {}
        public void comment(char[] values, int param, int param2) throws SAXException {}
        public void startCDATA() throws SAXException {}
        public void endCDATA() throws SAXException {}
    }
}

