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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.Events;
import org.netbeans.InvalidException;
import org.netbeans.Module;
import org.netbeans.ModuleFactory;
import org.netbeans.ModuleManager;
import org.netbeans.core.startup.ModuleSystem;

/**
 *
 * @author David Strupl
 */
public class JNLPModuleFactory extends ModuleFactory {
    
    private static final String MANIFEST_LOCATION = "META-INF/MANIFEST.MF";
    
    private static final String CONFIG_LOCATION = "META-INF/netbeans-location.properties";

    private static final Logger log = Logger.getLogger(JNLPModuleFactory.class.getName());
    
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
            log.log(Level.SEVERE, "Cannot scan manifests", ioe); // NOI18N
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
        String location = moduleLocations.get(module);
//        System.out.println("Factory creating prefixed " + module + " prefixURL == " + prefixURL + " location == " + location);
        try {
            return new ClasspathModule(mgr, ev, mani, history,  prefixURL, location, loader, this);
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "Cannot create classpath module.", ioe); // NOI18N
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
            log.log(Level.FINE, "Cannot convert JDK location to jar: protocol", e); // NOI18N
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
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Checking boot manifest: " + manifestUrlS);
                }

                InputStream is;
                try {
                    is = manifestUrl.openStream();
                } catch (IOException ioe) {
                    // Debugging for e.g. #32493 - which JAR was guilty?
                    log.log(Level.INFO, "Problem with URL: " + manifestUrl, ioe); // NOI18N
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
                            if (log.isLoggable(Level.FINE)) {
                                log.fine("Added prefix: " + prefix + " with location: " + location);
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
        
        log.warning("!!! WARNING: Jar " + 
            jarFileName + " with prefix " + prefix + 
                " is not specified in META-INF/netbeans-location.properties!"); 
        
        if (attr.getValue("OpenIDE-Module") != null) {
            log.warning("Assuming that Jar " + 
            jarFileName + " with prefix " + prefix + " is a regular module."); 
            return "modules";
        }
        log.warning("Putting Jar " + 
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
        log.warning(warning);
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
            log.warning("Module: " + module + 
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
                    log.log(Level.WARNING, "Cannot read: " + configURL, ioe); // NOI18N
                }
            }
        } catch (IOException ioe) {
            log.log(Level.WARNING, "Problem when getting config resources.", ioe);
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
                String loc1 = moduleLocations.get(m1.getCodeName());
                if (loc1 == null) {
                    log.warning("We don't have location for module " + m1); // NOI18N
                }
                if ((loc1 != null) &&
                    (loc1.startsWith("lib") ||
                     loc1.startsWith("core"))
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
            if (log.isLoggable(Level.FINE)) {
                log.fine("Constructing ClasspathDelegateClassLoader with prefixes: ");
                for (Iterator it = s.iterator(); it.hasNext(); ) {
                    log.fine(it.next().toString());
                }
                
            }
            classpathDelegateClassLoader = new ClasspathDelegateClassLoader(s, Module.class.getClassLoader(), mgr);
        }
        return classpathDelegateClassLoader;
    }
    
    @Override
    public boolean removeBaseClassLoader() {
        return true;
    }
}
