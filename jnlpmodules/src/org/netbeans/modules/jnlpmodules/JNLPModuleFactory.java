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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.netbeans.Events;
import org.netbeans.InvalidException;
import org.netbeans.Module;
import org.netbeans.ModuleFactory;
import org.netbeans.ModuleManager;
import org.netbeans.core.startup.ModuleSystem;
import org.openide.ErrorManager;

/**
 *
 * @author David Strupl
 */
public class JNLPModuleFactory extends ModuleFactory {
    
    private static final String MANIFEST_LOCATION = "META-INF/MANIFEST.MF";
    
    private static final String CONFIG_LOCATION = "META-INF/netbeans-location.properties";
    
    static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.modules.jnlpmodules"); // NOI18N

    // non module jars on classpath sorted by their original
    // location (location is a key, set of all jar names is
    // value in this map.
    private final Map<String, Set<String>> prefixedNonModules = new HashMap<String, Set<String>>();
    
    private final Map<String, String> prefixedModules = new HashMap<String, String>(); // module name --> prefix
    private final Map<String, String> moduleLocations = new HashMap<String, String>(); // module name --> location
    
    private Map<String, String> prefixLocations; // URL prefix --> location

    
    /** Creates a new instance of JNLPModuleFactory */
    public JNLPModuleFactory() {
        try {
            scanManifests();
        } catch (IOException ioe) {
            err.notify(ioe);
        }
    }

    @Override
    public Module create(java.io.File jar, Object history, boolean reloadable, boolean autoload, boolean eager, ModuleManager mgr, Events ev) throws IOException {
//        System.out.println("Factory creating standard " + jar);
//        return new StandardModule(mgr, ev, jar, history, reloadable, autoload, eager);
        throw new IOException("Standard modules not supported " + jar);
    }

    @Override
    public Module createFixed(Manifest mani, Object history, ClassLoader loader, boolean autoload, boolean eager,
            ModuleManager mgr, Events ev) throws InvalidException {
        Attributes attr = mani.getMainAttributes();
        String module = attr.getValue("OpenIDE-Module");
        String prefixURL = prefixedModules.get(module);
        String location = moduleLocations.get(module);;
//        System.out.println("Factory creating prefixed " + module + " prefixURL == " + prefixURL + " location == " + location);
        try {
            return new ClasspathModule(mgr, ev, mani, history,  prefixURL, location, loader, this);
        } catch (IOException ioe) {
            err.notify(ioe);
        }
        return null;
    }

    private void scanManifests() throws IOException {
        ClassLoader loader = ModuleSystem.class.getClassLoader();
        Collection<String> ignoredPrefixes = new ArrayList<String>(3);
        try {
            // skip the JDK/JRE libraries
            String jdk = System.getProperty("java.home");
            if (jdk.endsWith(File.separator + "jre")) { // NOI18N
                jdk = jdk.substring(0, jdk.length() - 4);
            }
            File f = new File(jdk);
            ignoredPrefixes.add("jar:" + f.toURI().toURL()); // NOI18N
            initializePrefixLocations(loader);
        } catch (MalformedURLException e) {
            err.notify(ErrorManager.INFORMATIONAL, e);
        }
        
        Enumeration<URL> e = loader.getResources(MANIFEST_LOCATION); // NOI18N
        
        // There will be duplicates: cf. #32576.
        Set<URL> checkedManifests = new HashSet<URL>();
        
        // first search for non-modules with prefixes
        MANIFESTS1:
            while (e.hasMoreElements()) {
                URL manifestUrl = e.nextElement();
                if (checkedManifests.contains(manifestUrl)) {
                    // Already seen, ignore.
                    continue;
                }
                String manifestUrlS = manifestUrl.toExternalForm();
                Iterator it = ignoredPrefixes.iterator();
                while (it.hasNext()) {
                    if (manifestUrlS.startsWith((String)it.next())) {
                        continue MANIFESTS1;
                    }
                }
                if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                    err.log("Checking boot manifest: " + manifestUrlS);
                }

                InputStream is;
                try {
                    is = manifestUrl.openStream();
                } catch (IOException ioe) {
                    // Debugging for e.g. #32493 - which JAR was guilty?
                    err.annotate(ioe, ErrorManager.UNKNOWN, "URL: " + manifestUrl, null, null, null); // NOI18N
                    throw ioe;
                }
                try {
                    Manifest mani = new Manifest(is);
                    Attributes attr = mani.getMainAttributes();
                    String mfurl =  manifestUrl.toExternalForm();
                    String prefix = mfurl.substring(0, mfurl.length() - MANIFEST_LOCATION.length());
                    String location = getLocation(prefix, attr);
                    if (location != null) {
                        if (attr.getValue("OpenIDE-Module") == null || // NOI18N
                                attr.getValue("OpenIDE-Archive-Locale") != null || // NOI18N
                                attr.getValue("OpenIDE-Archive-Branding") != null) { // NOI18N
                            addPrefixNonModule(prefix, location);
                            checkedManifests.add(manifestUrl);
                            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                                err.log("Added prefix: " + prefix + " with location: " + location);
                            }
                        } else { // module jar:
                            addPrefixModule(prefix, attr.getValue("OpenIDE-Module"), location);
                        }
                    }
                } finally {
                    is.close();
                }
            }
    }
    
    /**
     * Computes location of the original jar file containing the resource
     * with URL prefix prefix.
     */
    String getLocation(String prefix, Attributes attr) {
        if (prefixLocations == null) {
            return "core";
        }
        String jarFileName = getJarFileName(prefix, prefixLocations.keySet());
        if (jarFileName != null) {
            String loc = prefixLocations.get(jarFileName);
            if (loc != null) {
                return loc;
            }
        }
        
        err.log(ErrorManager.INFORMATIONAL, "!!! WARNING: Jar " + 
            jarFileName + " with prefix " + prefix + 
                " is not specified in META-INF/netbeans-location.properties!"); 
        
        if (attr.getValue("OpenIDE-Module") != null) {
            err.log(ErrorManager.INFORMATIONAL, "Assuming that Jar " + 
            jarFileName + " with prefix " + prefix + " is a regular module."); 
            return "modules";
        }
        err.log(ErrorManager.INFORMATIONAL, "Putting Jar " + 
            jarFileName + " with prefix " + prefix + " to JNLP classpath!!!");
        
        return "core";
    }
    
    private String getJarFileName(String prefix, Set jarFileNames) {
        for (Iterator it = jarFileNames.iterator(); it.hasNext(); ) {
            String jarFileName = (String)it.next();
            if (prefix.indexOf("RM")>=0) { // using "RM"+ is an ugly hack that should be removed
                if (prefix.indexOf("RM"+jarFileName) >= 0) {
                    return jarFileName;
                }
            } else { // "RM" is not part of the URL, try without this "anchor"
                if (prefix.indexOf(jarFileName) >= 0) {
                    return jarFileName;
                }
            }
        }
        String warning = "PROBLEM: " + prefix + " not found in "; // NOI18N
        for (Iterator it = jarFileNames.iterator(); it.hasNext(); ) {
            warning += (String)it.next() + ", ";
        }
        err.log(warning);
        return null;
    }
    
    String getJarFileName(String prefix) {
        return getJarFileName(prefix, prefixLocations.keySet());
    }
    
    public Map<String, String> getPrefixLocations() {
        return prefixLocations;
    }
    
    public void setPrefixLocations(Map<String, String> newPL) {
        prefixLocations = newPL;
    }
    
    /**
     * Calling this method requests to store the prefix and the
     * location of a non module jar.
     */
    public void addPrefixNonModule(String prefix, String location) {
        Set<String> s = prefixedNonModules.get(location);
        if (s == null) {
            s = new HashSet<String>();
            prefixedNonModules.put(location, s);
        }
        s.add(prefix);
    }
    
    /**
     * Calling this method requests to store the prefix and the
     * location of a non module jar.
     */
    public void addPrefixModule(String prefix, String module, String location) {
        String s = (String)prefixedModules.get(module);
        if (s == null) {
            prefixedModules.put(module, prefix);
            moduleLocations.put(module, location);
        } else {
            err.log(ErrorManager.WARNING, "Module: " + module + 
                " has more jars first: " + s + " second: " + prefix);
        }
    }
    
    /**
     * Returns all stored non-module jar prefixes
     * with given location.
     */
    public Set<String> getPrefixNonModules(String location) {
        Set<String> s = prefixedNonModules.get(location);
        if (s == null) {
            s = new HashSet<String>();
            prefixedNonModules.put(location, s);
        }
        return s;
    }
     /**
     * This method reads all config files that are stored on classpath
     * on the location CONFIG_LOCATION. The config file is in properties
     * format <jar file name>=<location in the NB installation dir>.
     * @param ClassLoader loader used for loading the config file.
     */
    private void initializePrefixLocations(ClassLoader loader) {
        Map<String, String> pl = new HashMap<String, String>();
        Properties p = new Properties();
        try {
            Enumeration<URL> e = loader.getResources(CONFIG_LOCATION);
            while (e.hasMoreElements()) {
                URL configURL = e.nextElement();
                try {
                    InputStream is = configURL.openStream();
                    p.load(is);
                    is.close();
                } catch (IOException ioe) {
                    err.notify(ioe);
                }
            }
        } catch (IOException ioe) {
            err.notify(ioe);
        }
        for (Object key : p.keySet()) {
            pl.put((String)key, (String)p.get(key));
        }
        setPrefixLocations(pl);
    }
    
    private ClassLoader classpathDelegateClassLoader;
    
    @Override
    public ClassLoader getClasspathDelegateClassLoader(ModuleManager mgr, ClassLoader del) {
        if (classpathDelegateClassLoader == null) {
            Set<String> s = getPrefixNonModules("core");
            s.addAll(getPrefixNonModules("lib"));
            Set m = mgr.getModules();
            for (Iterator it = m.iterator(); it.hasNext(); ) {
                Module m1 = (Module)it.next();
                if (  m1.getCodeName().equals("org.netbeans.bootstrap/1") ||
                      m1.getCodeName().equals("org.netbeans.core.startup/1") ||
                      m1.getCodeName().equals("org.netbeans.modules.jnlpmodules") ||
                      m1.getCodeName().equals("org.openide.modules") ||
                      m1.getCodeName().equals("org.openide.util") ||
                      m1.getCodeName().equals("org.openide.filesystems")
                ) {
                    if (m1 instanceof ClasspathModule) {
                        ClasspathModule cpm1 = (ClasspathModule)m1;
                        List<String> cp = new ArrayList<String>();
                        cp.add(cpm1.prefixURL);
                        cpm1.computePrefixes(cp);
                        s.addAll(cp);
                    }
                }
            }
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                err.log(ErrorManager.INFORMATIONAL, "Constructing ClasspathDelegateClassLoader with prefixes: ");
                for (Iterator it = s.iterator(); it.hasNext(); ) {
                    err.log(ErrorManager.INFORMATIONAL, it.next().toString());
                }
                
            }
            classpathDelegateClassLoader = new ClasspathDelegateClassLoader(s, Module.class.getClassLoader(), mgr);
        }
        return classpathDelegateClassLoader;
    }
    
    public boolean removeBaseClassLoader() {
        return true;
    }
}
