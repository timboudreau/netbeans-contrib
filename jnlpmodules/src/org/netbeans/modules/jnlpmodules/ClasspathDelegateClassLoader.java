/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.jnlpmodules;

import java.util.*;
import java.util.jar.Manifest;
import java.net.URL;
import java.io.*;
import java.net.MalformedURLException;
import java.security.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

/**
 * This classloader is used to directly delegate to the delegate class loader.
 * It does not define the classes by itself - everything is delegated. It allows
 * loading of the JDK classes plus the classes with given prefixes.
 * @author David Strupl
 */
public class ClasspathDelegateClassLoader extends URLPrefixClassLoader {

    /** Common prefix of all JDK jars */
    private String jdkPrefix = null;
    
    /** Creates a new instance of DelegatingClassLoader */
    public ClasspathDelegateClassLoader(Collection prefixes, ClassLoader delegate) {
        super(new ClassLoader[] { delegate }, false, prefixes, delegate);
        initJdkPrefixes();
    }

    /**
     * We do not delegate anything to the parent by code in ProxyClassLoader
     * but we do that ourselves from simpleFindClass.
     */
    protected boolean shouldDelegateResource(String pkg, ClassLoader parent) {
        return false;
    }
    
    protected boolean shouldBeCheckedAsParentProxyClassLoader() {
        return false;
    }
    /**
     * Overriden to directly delegate and not to define the classes here.
     */
    protected Class simpleFindClass(String name, String fileName, String pkgnameSlashes) {
        if (fileName == null) {
            fileName = name.replace('.', '/') + ".class";
        }
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
     * We accept the resource if it's either from JDK or normally using the
     * prefixes.
     */
    protected boolean acceptResourceURL(URL resourceURL, String resourceName) {
        
        if (resourceURL.toExternalForm().startsWith(jdkPrefix)) {
            return true;
        }
        
        return super.acceptResourceURL(resourceURL, resourceName);
    }

    /**
     * For debugging purposes.
     */
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
            e.printStackTrace();
        }
    }
}
