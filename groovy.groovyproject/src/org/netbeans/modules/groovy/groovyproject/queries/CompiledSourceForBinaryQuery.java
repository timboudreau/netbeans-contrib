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

package org.netbeans.modules.groovy.groovyproject.queries;

import java.io.File;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import java.net.URL;
import java.net.MalformedURLException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;

/**
 * Finds sources corresponding to binaries in a J2SE project.
 * @author Jesse Glick, Tomas Zezula
 */
public class CompiledSourceForBinaryQuery implements SourceForBinaryQueryImplementation {

    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private Map/*<URL,SourceForBinaryQuery.Result>*/  cache = new HashMap ();

    public CompiledSourceForBinaryQuery(AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.helper = helper;
        this.evaluator = evaluator;
    }

    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        if (FileUtil.getArchiveFile(binaryRoot) != null) {
            binaryRoot = FileUtil.getArchiveFile(binaryRoot);
            // XXX check whether this is really the root
        }
        SourceForBinaryQuery.Result res = (SourceForBinaryQuery.Result) cache.get (binaryRoot);
        if (res != null) {
            return res;
        }
        String srcPropName = null;
        if (hasSources(binaryRoot,"build.classes.dir")) {   //NOI18N
            srcPropName = "src.dir";                        //NOI18N
        }
        else if (hasSources (binaryRoot,"dist.jar")) {      //NOI18N
            srcPropName = "src.dir";                        //NOI18N
        }
        if (srcPropName == null) {
            return null;
        }
        else {
            res = new Result (this.helper, this.evaluator, srcPropName);
            cache.put (binaryRoot, res);
            return res;
        }
    }


    private boolean hasSources (URL binaryRoot, String binaryProperty) {
        try {
            String outDir = evaluator.getProperty(binaryProperty);
            if (outDir != null) {
                File f = helper.resolveFile (outDir);
                URL url = f.toURI().toURL();
                if (!f.exists() && !f.getPath().toLowerCase().endsWith(".jar")) { // NOI18N
                    // non-existing 
                    assert !url.toExternalForm().endsWith("/") : f; // NOI18N
                    url = new URL(url.toExternalForm() + "/"); // NOI18N
                }
                if (url.equals (binaryRoot)) {
                    return true;
                }
            }
        } catch (MalformedURLException malformedURL) {
            ErrorManager.getDefault().notify(malformedURL);
        }
        return false;
    }
    
    private static class Result implements SourceForBinaryQuery.Result, PropertyChangeListener {

        private FileObject[] cache;
        private AntProjectHelper helper;
        private PropertyEvaluator evaluator;
        private String propName;
        private ArrayList listeners;

        public Result (AntProjectHelper helper, PropertyEvaluator evaluator, String propName) {
            this.helper = helper;
            this.evaluator = evaluator;
            this.propName = propName;
            this.evaluator.addPropertyChangeListener(this);
        }
        
        public synchronized FileObject[] getRoots () {
            if (this.cache == null) {
                String srcDir = this.evaluator.getProperty(this.propName);
                FileObject srcFile = null;
                if (srcDir != null) {
                    srcFile = this.helper.resolveFileObject(srcDir);
                    if (srcFile != null) {
                        if (!srcFile.isValid()) {
                            srcFile = null;
                        }
                        else if (FileUtil.isArchiveFile(srcFile)) {
                            srcFile = FileUtil.getArchiveRoot (srcFile);
                        }
                    }
                }               
                this.cache = (srcFile == null ? new FileObject[0] : new FileObject[] {srcFile});
            }
            return this.cache;
        }
        
        public synchronized void addChangeListener (ChangeListener l) {
            if (this.listeners == null) {
                this.listeners = new ArrayList();
            }
            this.listeners.add (l);
        }
        
        public synchronized void removeChangeListener (ChangeListener l) {
            if (this.listeners == null) {
                return;
            }
            this.listeners.remove (l);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (propName.equals(evt.getPropertyName())) {
                synchronized(this) {
                    this.cache = null;
                }
                this.fireChange ();
            }
        }



        private void fireChange() {
            Iterator it;
            synchronized (this) {
                if (this.listeners == null) {
                    return;
                }
                it = ((ArrayList)this.listeners.clone()).iterator();
            }
            ChangeEvent event = new ChangeEvent(this);
            while (it.hasNext()) {
                ((ChangeListener)it.next()).stateChanged(event);
            }
        }

    }
    
}
