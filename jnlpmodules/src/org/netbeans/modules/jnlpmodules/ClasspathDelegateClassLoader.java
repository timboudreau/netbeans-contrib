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

import java.util.*;
import java.util.jar.Manifest;
import java.net.URL;
import java.io.*;
import java.net.MalformedURLException;
import java.security.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import org.netbeans.ModuleManager;

/**
 * This classloader is used to directly delegate to the delegate class loader.
 * It does not define the classes by itself - everything is delegated. It allows
 * loading of the JDK classes plus the classes with given prefixes.
 * @author David Strupl
 */
public class ClasspathDelegateClassLoader extends URLPrefixClassLoader {

    /** Common prefix of all JDK jars */
    private String jdkPrefix = null;
    
    private ModuleManager mgr;
    
    /** Creates a new instance of DelegatingClassLoader */
    public ClasspathDelegateClassLoader(Collection prefixes, ClassLoader delegate, ModuleManager manager) {
        super(new ClassLoader[] { delegate }, false, prefixes, delegate);
        initJdkPrefixes();
        this.mgr = manager;
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

    protected boolean isSpecialResource(String pkg) {
        if (mgr != null && mgr.isSpecialResource(pkg)) {
            return true;
        }
        return super.isSpecialResource(pkg);
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
