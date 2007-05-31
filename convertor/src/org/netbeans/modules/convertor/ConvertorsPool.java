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

package org.netbeans.modules.convertor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.netbeans.api.convertor.ConvertorDescriptor;
import org.netbeans.api.convertor.ConvertorException;
import org.netbeans.api.convertor.Convertors;
import org.netbeans.spi.convertor.Convertor;
import org.openide.ErrorManager;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 *
 * @author  David Konecny
 */
public class ConvertorsPool implements LookupListener {

    public static final String NETBEANS_CONVERTOR = "NetBeans-Convertor"; // NOI18N
    public static final String NETBEANS_SIMPLY_CONVERTIBLE = "NetBeans-Simply-Convertible"; // NOI18N
    public static final String PROPERTIES_CONVERTOR = "PropertiesConvertor"; // NOI18N
    
    private static final ConvertorsPool DEFAULT = new ConvertorsPool();
    
    private Set convertorDescriptors = new HashSet();
    
    private boolean initialized = false;

    private Lookup.Result modules;
    
    private ConvertorsPool() {
        modules = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class)); 
        modules.allItems();
        modules.addLookupListener(this);
    }

    public static ConvertorsPool getDefault() {
        return DEFAULT;
    }
    
    public ConvertorDescriptor getReadConvertor(String namespace, String element) {
        assert namespace != null && element != null;
        initConvertors();
        Iterator it = convertorDescriptors.iterator();
        while (it.hasNext()) {
            ConvertorDescriptor cd = (ConvertorDescriptor)it.next();
            if (namespace.equals(cd.getNamespace()) &&
                element.equals(cd.getElementName())) {
                return cd;
            }
        }
        return null;
    }

    public ConvertorDescriptor getWriteConvertor(Object o) {
        initConvertors();
        Convertor convertor = null;
        Class clazz = o.getClass();
        Iterator it = convertorDescriptors.iterator();
        while (it.hasNext()) {
            ConvertorDescriptor cd = (ConvertorDescriptor)it.next();
            if (cd.getClassName() == null || (!cd.getClassName().equals(o.getClass().getName()))) {
                continue;
            }
            Class cls;
            try {
                cls = InstanceUtils.findClass(cd.getClassName());
                
                // ClassLoader can be null for primitive types (e.g. java.lang.Integer)
                // No idea why, but if it is we will just skip the ClassLoader test.
                ClassLoader clsLoader = cls.getClassLoader();
                if (clsLoader != null && !(clsLoader.loadClass(o.getClass().getName()) == o.getClass())) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Object "+o+" cannot be stored by convertor "+cd+", because of classloader mismatch. Skipping convertor."); // NOI18N
                    continue;
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                continue;
            }
            
            if (clazz.equals(cls)) {
                return cd;
            }
        }
        return null;
    }
    
    public Set getDescriptors() {
        initConvertors();
        return new HashSet(convertorDescriptors);
    }
    
    private synchronized void initConvertors() {
        if (initialized) {
            return;
        }
        loadConvertors();
        initialized = true;
    }
    
    public synchronized void resultChanged(LookupEvent ev) {
        loadConvertors();
    }
    
    private void loadConvertors() {
        Set old = convertorDescriptors;
            
        ClassLoader currentClassLoader = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
        Set convs = new HashSet();
        Enumeration en = null;
        try {
            en = currentClassLoader.getResources("META-INF/MANIFEST.MF"); // NOI18N
        } catch (IOException ex) {
            ex.printStackTrace();
            ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            return;
        }
        while (en.hasMoreElements ()) {
            URL u = (URL)en.nextElement();
            Manifest mf;

            try {
                InputStream is = u.openStream();
                try {
                    mf = new Manifest(is);
                    loadConvertors(mf, convs);
                } finally {
                    is.close();
                }
            } catch (IOException ex) {
                ErrorManager.getDefault().log(ErrorManager.ERROR, "Cannot read file "+u+". The file will be ignored."); // NOI18N
            }

        }
        convertorDescriptors = convs;
            
        Accessor.DEFAULT.firePropertyChange(Convertors.CONVERTOR_DESCRIPTORS, old, new HashSet(convertorDescriptors));
    }

    private void loadConvertors(Manifest m, Set convs) {
        Iterator it = m.getEntries().entrySet().iterator(); // Iterator<Map.Entry<String,Attributes>>
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String name = (String)entry.getKey();
            Attributes attrs = (Attributes)entry.getValue();
            if (attrs.getValue(NETBEANS_CONVERTOR) != null) {
                String convertor = getClassName(name);
                String conv;
                int index = 0;
                while (null != (conv = attrs.getValue(appendNumber(NETBEANS_CONVERTOR, index)))) {
                    // parse following format:  "{namespace}element, class"
                    int endOfNamespace = conv.indexOf('}');
                    if (endOfNamespace == -1) {
                        ErrorManager.getDefault().log(ErrorManager.WARNING, "Attribute "+ // NOI18N
                            appendNumber(NETBEANS_CONVERTOR, index)+
                            " of convertor "+convertor+ // NOI18N
                            " does not contain namespace: "+conv); // NOI18N
                        break;
                    }
                    int startOfClass = conv.indexOf(',');
                    String namespace = conv.substring(1, endOfNamespace);
                    String rootElement;
                    if (startOfClass == -1) {
                        rootElement = conv.substring(endOfNamespace+1);
                    } else {
                        rootElement = conv.substring(endOfNamespace+1, startOfClass);
                    }
                    rootElement = rootElement.trim();
                    if (rootElement.length() == 0) {
                        ErrorManager.getDefault().log(ErrorManager.WARNING, "Attribute "+ // NOI18N
                            appendNumber(NETBEANS_CONVERTOR, index)+
                            " of convertor "+convertor+ // NOI18N
                            " does not contain element: "+conv); // NOI18N
                        break;
                    }
                    String writes = null;
                    if (startOfClass != -1) {
                        writes = conv.substring(startOfClass+1).trim();
                    }

                    convs.add(Accessor.DEFAULT.createConvertorDescriptor(
                        new ProxyConvertor(convertor, namespace, rootElement, writes), namespace, rootElement, writes));
                    index++;
                }
            }
            if (attrs.getValue(NETBEANS_SIMPLY_CONVERTIBLE) != null) {
                String conv = attrs.getValue(NETBEANS_SIMPLY_CONVERTIBLE);
                String convertor = PROPERTIES_CONVERTOR;
                // parse following format:  "{namespace}element"
                int endOfNamespace = conv.indexOf('}');
                if (endOfNamespace == -1) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Attribute "+ // NOI18N
                        NETBEANS_SIMPLY_CONVERTIBLE+
                        " for class "+name+ // NOI18N
                        " does not contain namespace: "+conv); // NOI18N
                    continue;
                }
                String namespace = conv.substring(1, endOfNamespace);
                String rootElement = conv.substring(endOfNamespace+1);
                rootElement = rootElement.trim();
                if (rootElement.length() == 0) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "Attribute "+ // NOI18N
                        NETBEANS_SIMPLY_CONVERTIBLE+
                        " for class "+name+ // NOI18N
                        " does not contain element: "+conv); // NOI18N
                    continue;
                }
                String writes = getClassName(name);
                convs.add(Accessor.DEFAULT.createConvertorDescriptor(
                    new ProxyConvertor(convertor, namespace, rootElement, writes), namespace, rootElement, writes));
            }
        }
    }
    
    private String getClassName(String className) {
        className = className.replace('/', '.');
        // this will remove ".class" and everything behind it
        className = className.substring(0, className.indexOf(".class")); // NOI18N
        return className;
    }
    
    private String appendNumber(String name, int number) {
        if (number == 0) {
            return name;
        } else {
            return name + "-" + Integer.toString(number+1);
        }
    }
    
    private static class ProxyConvertor implements Convertor {
        
        private String convertor;
        private String namespace;
        private String rootElement;
        private String writes;

        private Convertor delegate;
        
        public ProxyConvertor(String convertor, String namespace, String rootElement, String writes) {
            this.convertor = convertor;
            this.namespace = namespace;
            this.rootElement = rootElement;
            this.writes = writes;
        }
        
        public Object read(Element e) {
            loadRealConvertor();
            if (delegate == null) {
                throw new ConvertorException("Cannot read element. The convertor class "+convertor+" cannot be instantiated."); // NOI18N
            }
            return delegate.read(e);
        }
        
        public Element write(Document doc, Object inst) {
            loadRealConvertor();
            if (delegate == null) {
                throw new ConvertorException("Cannot persist object. The convertor class "+convertor+" cannot be instantiated."); // NOI18N
            }
            return delegate.write(doc, inst);
        }
        
        private synchronized void loadRealConvertor() {
            if (delegate == null) {
                if (convertor.equals(PROPERTIES_CONVERTOR)) {
                    delegate = new PropertiesConvertor(namespace, rootElement, writes);
                } else {
                    try {
                        Class c = InstanceUtils.findClass(convertor);
                        delegate = (Convertor)c.newInstance();
                    } catch (Exception e) {
                        ErrorManager.getDefault().log(ErrorManager.WARNING, e.toString());
                    }
                }
            }
        }
        
        public String toString() {
            return "ProxyConvertor[convertor="+convertor+", namespace="+namespace+", rootElement="+ // NOI18N
                rootElement+", writes="+writes+", delegate="+delegate+"] " + super.toString(); // NOI18N
        }
        
    }
    
}
