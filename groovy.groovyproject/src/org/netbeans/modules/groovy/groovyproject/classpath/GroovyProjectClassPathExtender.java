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

package org.netbeans.modules.groovy.groovyproject.classpath;

import java.io.IOException;
import java.io.File;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.GroovyProjectProperties;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.VisualClassPathItem;

public class GroovyProjectClassPathExtender {
        // implements ProjectClassPathExtender {
    
    private static final String CP_CLASS_PATH = "javac.classpath"; //NOI18N

    private Project project;
    private AntProjectHelper helper;
    private ReferenceHelper refHelper;
    private PropertyEvaluator eval;

    public GroovyProjectClassPathExtender (Project project, AntProjectHelper helper, PropertyEvaluator eval, ReferenceHelper refHelper) {
        this.project = project;
        this.helper = helper;
        this.eval = eval;
        this.refHelper = refHelper;
    }

    public boolean addLibrary(final Library library) throws IOException {
        assert library != null : "Parameter can not be null";       //NOI18N
        try {
            return ((Boolean)ProjectManager.mutex().writeAccess(
                    new Mutex.ExceptionAction () {
                        public Object run() throws Exception {
                            EditableProperties props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
                            String raw = props.getProperty(CP_CLASS_PATH);
                            GroovyProjectProperties.PathParser parser = new GroovyProjectProperties.PathParser ();
                            List resources = (List) parser.decode(raw, helper, eval, refHelper);
                            VisualClassPathItem item = VisualClassPathItem.create (library);
                            if (!resources.contains(item)) {
                                resources.add (item);
                                raw = parser.encode (resources, helper, refHelper);
                                props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);    //PathParser may change the EditableProperties
                                props.put (CP_CLASS_PATH, raw);
                                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                                ProjectManager.getDefault().saveProject(project);
                                return Boolean.TRUE;
                            }
                            return Boolean.FALSE;
                        }
                    }
            )).booleanValue();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            else {
                Exception t = new IOException ();
                throw (IOException) ErrorManager.getDefault().annotate(t,e);
            }
        }
    }

    public boolean addArchiveFile(final FileObject archiveFile) throws IOException {
        assert archiveFile != null : "Parameter can not be null";       //NOI18N
        try {
            return ((Boolean)ProjectManager.mutex().writeAccess(
                    new Mutex.ExceptionAction () {
                        public Object run() throws Exception {
                            EditableProperties props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
                            String raw = props.getProperty(CP_CLASS_PATH);
                            GroovyProjectProperties.PathParser parser = new GroovyProjectProperties.PathParser ();
                            List resources = (List) parser.decode(raw, helper, eval, refHelper);
                            File f = FileUtil.toFile (archiveFile);
                            if (f == null ) {
                                throw new IllegalArgumentException ("The file must exist on disk");     //NOI18N
                            }
                            VisualClassPathItem item = VisualClassPathItem.create (f);
                            if (!resources.contains(item)) {
                                resources.add (item);
                                raw = parser.encode (resources, helper, refHelper);
                                props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);  //PathParser may change the EditableProperties
                                props.put (CP_CLASS_PATH, raw);
                                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
                                ProjectManager.getDefault().saveProject(project);
                                return Boolean.TRUE;
                            }
                            return Boolean.FALSE;
                        }
                    }
            )).booleanValue();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            else {
                Exception t = new IOException ();
                throw (IOException) ErrorManager.getDefault().annotate(t,e);
            }
        }
    }

    public boolean addAntArtifact(final AntArtifact artifact) throws IOException {
        assert artifact != null : "Parameter can not be null";       //NOI18N
        try {
            return ((Boolean)ProjectManager.mutex().writeAccess(
                    new Mutex.ExceptionAction () {
                        public Object run() throws Exception {
                            EditableProperties props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);                            
                            String raw = props.getProperty(CP_CLASS_PATH);
                            GroovyProjectProperties.PathParser parser = new GroovyProjectProperties.PathParser ();
                            List resources = (List) parser.decode(raw, helper, eval, refHelper);
                            VisualClassPathItem item = VisualClassPathItem.create (artifact);
                            if (!resources.contains(item)) {
                                resources.add (item);
                                raw = parser.encode (resources, helper, refHelper);                                
                                props = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);    //Reread the properties, PathParser changes them
                                props.put (CP_CLASS_PATH, raw);
                                helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);                                
                                ProjectManager.getDefault().saveProject(project);
                                return Boolean.TRUE;
                            }
                            return Boolean.FALSE;
                        }
                    }
            )).booleanValue();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            else {
                Exception t = new IOException ();
                throw (IOException) ErrorManager.getDefault().annotate(t,e);
            }
        }
    }

}
