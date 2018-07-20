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
package org.netbeans.modules.jnlpmodules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.net.URL;
import java.io.*;
import java.net.MalformedURLException;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.ModuleManager;
import org.netbeans.Module;

/**
 * This classloader is used to directly delegate to the delegate class loader.
 * It does not define the classes by itself - everything is delegated. It allows
 * loading of the JDK classes plus the classes with given prefixes.
 * @author David Strupl
 */
public class ClasspathDelegateClassLoader extends ClassLoader {
    
    private static final Logger log = Logger.getLogger(ClasspathDelegateClassLoader.class.getName());
    
    /** Common prefix of all JDK jars */
    private String jdkPrefix = null;
    
    private ModuleManager mgr;
    
    private Collection<String> prefixes;
    
    private ClassLoader delegate;
    
    /** Creates a new instance of DelegatingClassLoader */
    public ClasspathDelegateClassLoader(Collection<String> prefixes, ClassLoader delegate, ModuleManager manager) {
        super();
        initJdkPrefixes();
        this.mgr = manager;
        this.prefixes = new HashSet<String>(prefixes);
        this.delegate = delegate;
        mgr.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                addNewModules();
            }
        });
    }

    @Override
    protected synchronized Class loadClass(String name, boolean resolve)
                                            throws ClassNotFoundException {
        Class cls = findLoadedClass(name);
        if (cls != null) {
            return cls;
        }
        int last = name.lastIndexOf('.');
        if (last == -1) {
            throw new ClassNotFoundException("Will not load classes from default package (" + name + ")"); // NOI18N
        }

        // Strip+intern or use from package coverage
        String pkg = (last >= 0) ? name.substring(0, last) : ""; 
        
        cls = doLoadClass(pkg, name);
        
        if (cls == null) throw new ClassNotFoundException(name); 
        if (resolve) resolveClass(cls); 
        return cls; 
    }
    /**
     * Overriden to directly delegate and not to define the classes here.
     */
    private Class doLoadClass(String pkg, String name) {
        String fileName = name.replace('.', '/') + ".class";
        try {
            for (Enumeration en = delegate.getResources(fileName); en.hasMoreElements(); ) {
                URL url = (URL)en.nextElement();
                if (acceptResourceURL(url, fileName)) {
                    try {
                        return delegate.loadClass(name);
                    } catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                        // try all of them
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }   
    /**
     * Finds the named resource in the delegate classloader. If there are more
     * resources with the same name the first for which method acceptResourceURL
     * returned true is returned.
     */
    @Override
    protected URL findResource(String name) {

        try {
            for (Enumeration en = delegate.getResources(name); en.hasMoreElements(); ) {
                URL url = (URL)en.nextElement();
                if (acceptResourceURL(url, name)) {
                    return url;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /** Allows to specify the right permissions, subclasses can do it differently.
     */
    protected PermissionCollection getPermissions( CodeSource cs ) {           
        return Policy.getPolicy().getPermissions(cs);       
    }        
    
    /**
     * We accept the resource if it's either from JDK or normally using the
     * prefixes.
     */
    private boolean acceptResourceURL(URL resourceURL, String resourceName) {
        
        if (resourceURL.toExternalForm().startsWith(jdkPrefix)) {
            return true;
        }
        
        String urlString = resourceURL.toExternalForm();
        String localPrefix = urlString.substring(0, urlString.length() - resourceName.length());
        return prefixes.contains(localPrefix);
    }
    
    /**
     * For debugging purposes.
     */
    @Override
    public String toString() {
        return "ClasspathDelegateClassLoader:" + super.toString();
    }
    
    /**
     * The JDK prefix is computed from java.home system property - it should
     * be always defined according to the JLF.
     */
    private void initJdkPrefixes() {
        try {
            String jdk = System.getProperty("java.home");
            if (jdk.endsWith(File.separator + "jre")) { // NOI18N
                jdk = jdk.substring(0, jdk.length() - 4);
            }
            File f = new File(jdk);
            jdkPrefix = "jar:" + f.toURI().toURL(); // NOI18N
        } catch (MalformedURLException e) {
            log.log(Level.WARNING, "", e); // NOI18N
        }
    }
    
    private void addNewModules() {
        Set m = mgr.getModules();
        for (Iterator it = m.iterator(); it.hasNext(); ) {
            Module m1 = (Module)it.next();
            if (m1 instanceof ClasspathModule) {
                ClasspathModule cpm1 = (ClasspathModule)m1;
                if (((cpm1.location != null)) && 
                    (cpm1.location.startsWith("lib") ||
                     cpm1.location.startsWith("core")
                    )) {
                    List<String> cp = new ArrayList<String>();
                    cp.add(cpm1.prefixURL);
                    cpm1.computePrefixes(cp);
                    prefixes.addAll(cp);
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("ClasspathDelegateClassLoader adding " + cp); // NOI18N
                    }
                }
            }
        }
    }

}
