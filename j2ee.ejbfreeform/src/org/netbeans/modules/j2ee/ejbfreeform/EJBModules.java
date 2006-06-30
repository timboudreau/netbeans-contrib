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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarFactory;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarsInProject;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.w3c.dom.Element;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;


/**
 * EJB module implementation on top of freeform project.
 *
 * @author  Pavel Buzek
 */
public class EJBModules implements EjbJarProvider, EjbJarsInProject, AntProjectListener, ClassPathProvider {
    
    private ArrayList modules = new ArrayList ();
    private HashMap cache = new HashMap ();
    private Project project;
    private AntProjectHelper helper;
    private PropertyEvaluator evaluator;
    
    public EJBModules (Project project, AntProjectHelper helper, PropertyEvaluator evaluator) {
        assert project != null;
        this.project = project;
        this.helper = helper;
        this.evaluator = evaluator;
        helper.addAntProjectListener(this);
    }
    
    public EjbJar findEjbJar (FileObject file) {
        Project owner = FileOwnerQuery.getOwner (file);
        synchronized (this) {
            if (project.equals (owner)) {
                if (modules.isEmpty()) {
                    readAuxData ();
                }
                for (Iterator iter = modules.iterator (); iter.hasNext ();) {
                    FFEJBModule wm = (FFEJBModule) iter.next ();
                    if (wm.contais (file)) {
                        if (cache.get (wm) == null) {
                            cache.put (wm, EjbJarFactory.createEjbJar (wm));
                        }
                        return (EjbJar) cache.get (wm);
                    }
                }
            }
            return null;
        }
    }

    public ClassPath findClassPath (FileObject file, String type) {
        // findClassPath() should return null for any other type than
        // ClassPath.SOURCE, see #59031
        if (!ClassPath.SOURCE.equals (type)) {
            return null;
        }
        Project owner = FileOwnerQuery.getOwner (file);
        if (owner != null && owner.equals (project)) {
            if (modules == null) {
                readAuxData ();
            }
            for (Iterator iter = modules.iterator (); iter.hasNext ();) {
                FFEJBModule wm = (FFEJBModule) iter.next ();
                if (wm.contais (file)) {
                    return wm.findClassPath (file, type);
                }
            }
        }
        return null;
    }
    
    public synchronized void readAuxData () {
        modules.clear();
        cache.clear();
        AuxiliaryConfiguration aux = (AuxiliaryConfiguration)project.getLookup().lookup(AuxiliaryConfiguration.class);
        assert aux != null;
        Element ejb = aux.getConfigurationFragment("ejb-data", EJBProjectNature.NS_EJB, true);
        if (ejb == null) {
            return;
        }
        List/*<Element>*/ ejbModules = Util.findSubElements(ejb);
        Iterator it = ejbModules.iterator();
        while (it.hasNext()) {
            Element ejbModulesEl = (Element)it.next();
            assert ejbModulesEl.getLocalName().equals("ejb-module") : ejbModulesEl;
            FileObject configFilesFO = getFile (ejbModulesEl, "config-files"); //NOI18N
            Element j2eeSpecEl = Util.findElement (ejbModulesEl, "j2ee-spec-level", EJBProjectNature.NS_EJB);
            String j2eeSpec = j2eeSpecEl == null ? null : evaluator.evaluate (Util.findText (j2eeSpecEl));
//            Element contextPathEl = Util.findElement (ejbModulesEl, "context-path", EJBProjectNature.NS_WEB);
//            String contextPathText = contextPathEl == null ? null : Util.findText (contextPathEl);
//            String contextPath = contextPathText == null ? null : evaluator.evaluate (contextPathText);
            Element classpathEl = Util.findElement (ejbModulesEl, "classpath", EJBProjectNature.NS_EJB);
            ClassPath cp = classpathEl == null ? null : createClasspath (classpathEl);
            FileObject [] sources = getSources (classpathEl);
            modules.add (new FFEJBModule (configFilesFO, j2eeSpec, /*contextPath,*/ sources, cp));
        }
    }
    
    private FileObject getFile (Element parent, String fileElName) {
        Element el = Util.findElement (parent, fileElName, EJBProjectNature.NS_EJB);
        String fname = Util.findText (el);
        String locationEval = evaluator.evaluate(fname);
        if (locationEval != null) {
            File locationFile = helper.resolveFile(locationEval);
            return FileUtil.toFileObject(locationFile);
        }
        return null;
    }

    private FileObject [] getSources (Element classpathEl) {
        String cp = Util.findText(classpathEl);
        if (cp == null) {
            cp = "";
        }
        String cpEval = evaluator.evaluate(cp);
        if (cpEval == null) {
            return null;
        }
        String[] path = PropertyUtils.tokenizePath(cpEval);
        Set srcRootSet = new HashSet ();
        for (int i = 0; i < path.length; i++) {
            File entryFile = helper.resolveFile(path[i]);
            URL entry;
            try {
                entry = entryFile.toURI().toURL();
                if (!entryFile.exists () && !entry.toExternalForm ().endsWith ("/")) {
                        entry = new URL (entry.toExternalForm () + "/");
                }
            } catch (MalformedURLException x) {
                throw new AssertionError(x);
            }
            if (FileUtil.isArchiveFile(entry)) {
                entry = FileUtil.getArchiveRoot(entry);
            }
            SourceForBinaryQuery.Result res = SourceForBinaryQuery.findSourceRoots (entry);
            FileObject srcForBin [] = res.getRoots ();
            for (int j = 0; j < srcForBin.length; j++) {
                srcRootSet.add (srcForBin [j]);
            }
            if (srcForBin.length == 0) {
                srcRootSet.add(FileUtil.toFileObject(entryFile));
            }
        }
        SourceGroup sg [] = ProjectUtils.getSources (project).getSourceGroups (JavaProjectConstants.SOURCES_TYPE_JAVA);
        Set filteredSources = new HashSet ();
        // all Java sources should be added to the result if the classpath element is empty
        for (int i = 0; i < sg.length; i++) {
            if (path.length == 0 || srcRootSet.contains (sg [i].getRootFolder ())) {
                filteredSources.add (sg [i].getRootFolder ());
            }
        }
        // XXX if the classpath element is empty the result will contain the test roots
        return (FileObject []) filteredSources.toArray (new FileObject [filteredSources.size ()]);
    }
    
    /**
     * Create a classpath from a &lt;classpath&gt; element.
     */
    private ClassPath createClasspath(Element classpathEl) {
        String cp = Util.findText(classpathEl);
        if (cp == null) {
            cp = "";
        }
        String cpEval = evaluator.evaluate(cp);
        if (cpEval == null) {
            return null;
        }
        String[] path = PropertyUtils.tokenizePath(cpEval);
        URL[] pathURL = new URL[path.length];
        for (int i = 0; i < path.length; i++) {
            File entryFile = helper.resolveFile(path[i]);
            URL entry;
            try {
                entry = entryFile.toURI().toURL();
                if (FileUtil.isArchiveFile(entry)) {
                    entry = FileUtil.getArchiveRoot(entry);
                } else {
                    String s = entry.toExternalForm();
                    if (!s.endsWith("/")) { // NOI18N
                        // Folder which is not built.
                        entry = new URL(s + '/');
                    }
                }
            } catch (MalformedURLException x) {
                throw new AssertionError(x);
            }
            pathURL[i] = entry;
        }
        // create a null classpath instead of an empty one 
        // in order to allow the Java nature to return its classpath
        return pathURL.length > 0 ? ClassPathSupport.createClassPath(pathURL) : null;
    }
    
    public void configurationXmlChanged(AntProjectEvent ev) {
        readAuxData();
    }
    
    public void propertiesChanged(AntProjectEvent ev) {
        // ignore
    }

    public EjbJar[] getEjbJars() {
        if (modules.isEmpty()) {
            readAuxData ();
        }
        EjbJar results [] = new EjbJar[modules.size()];
        int i = 0;
        for (Iterator iter = modules.iterator (); iter.hasNext ();) {
            FFEJBModule ejbm = (FFEJBModule) iter.next ();
            if (cache.get (ejbm) == null) {
                results[i] = EjbJarFactory.createEjbJar (ejbm);
                cache.put (ejbm, results[i]);
            } else {
                results[i] = (EjbJar) cache.get(ejbm);
            }
        }
        return results;
    }
    
    private final class FFEJBModule implements EjbJarImplementation {
        
//        public static final String FOLDER_META_INF = "META-INF";//NOI18N
        public static final String FILE_DD        = "ejb-jar.xml";//NOI18N
    
        private FileObject configFilesFO;
        private FileObject [] sourcesFOs;
        private ClassPath classPath;
        private String j2eeSpec;
//        private String contextPath;
        
        FFEJBModule (FileObject configFilesFO, String j2eeSpec, /*String contextPath,*/ FileObject sourcesFOs[], ClassPath classPath) {
            this.configFilesFO = configFilesFO;
            this.j2eeSpec = j2eeSpec;
//            this.contextPath = contextPath;
            this.sourcesFOs = sourcesFOs;
            this.classPath = classPath;
        }
        
        boolean contais (FileObject fo) {
            if (configFilesFO == fo || FileUtil.isParentOf (configFilesFO , fo))
                return true;
            for (int i = 0; i < sourcesFOs.length; i++) {
                if (sourcesFOs [i] == fo || FileUtil.isParentOf (sourcesFOs [i], fo))
                    return true;
            }
            return false;
        }
        
//        public FileObject getDocumentBase () {
//            return configFilesFO;
//        }
        
        public ClassPath findClassPath (FileObject file, String type) {
            return classPath;
        }
        
        public String getJ2eePlatformVersion () {
            return j2eeSpec;
        }
        
//        public String getContextPath () {
//            return contextPath;
//        }
        
        public String toString () {
            StringBuffer sb = new StringBuffer ("EJB module in freeform project" +
                "\n\tconfig files:" + configFilesFO.getPath () + 
//                "\n\tcontext path:" + contextPath +
                "\n\tj2ee version:" + j2eeSpec);
            for (int i = 0; i < sourcesFOs.length; i++) {
                sb.append ("\n\tsource root:" + sourcesFOs [i].getPath ());
            }
            return sb.toString ();
        }
        
        public FileObject getDeploymentDescriptor () {
            return getMetaInf ().getFileObject (FILE_DD);
        }
        
        public FileObject getMetaInf () {
            return configFilesFO;
        }

        public FileObject[] getJavaSources() {
            return sourcesFOs;
        }
        
    }
}
