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

package org.netbeans.modules.javafx.project.ui.customizer;

import org.netbeans.modules.javafx.project.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 *
 * @author alex
 */
public class WebStartTest extends NbTestCase {
    protected File dataDir;
    
    public WebStartTest(String testName) {
        super(testName);
    }

    public static Test suite(){
        TestSuite suite = new TestSuite(WebStartTest.class);
        return suite;
    }



    @Override
    public void setUp() throws Exception {
        clearWorkDir();
        dataDir = new File(getClass().getResource("data").getFile());
        //System.out.println("dataDir = " + dataDir.getPath());
    }

    @Override
    public void tearDown() throws Exception {
    }
    
    
    public void testWebStart() throws Exception {
        String name = "WebStartFXProject";
        String mainClass = name.toLowerCase() + ".Main";
        //System.out.println("WorkDir = " + getWorkDir());
        AntProjectHelper aph = JavaFXProjectGenerator.createProject(new File(getWorkDir(), name), name, mainClass, "manifest.mf");
        assertNotNull(aph);
        //String projectDirectory = aph.getProjectDirectory().getName();
        //System.out.println("ProjectDirectory = " + aph.getProjectDirectory().getName());
        
        JavaFXProject prj = (JavaFXProject) ProjectManager.getDefault().findProject(aph.getProjectDirectory());
        assertNotNull(prj);
        //System.out.println("prj = "+ prj.getProjectDirectory().getNameExt());
        //System.out.println("Path = "+ prj.getProjectDirectory().getFileObject("nbproject/project.properties").getPath());
        //System.out.println("properties = "+ aph.getProperties(prj.getProjectDirectory().getFileObject("nbproject/project.properties").getPath()));
        WebStartProjectProperties wsProperties = new WebStartProjectProperties(prj, prj.evaluator());
        assertNotNull(wsProperties);
        //System.out.println("JNLP_ENABLED = "+ Boolean.valueOf(wsProperties.getProperty(WebStartProjectProperties.JNLP_ENABLED)).booleanValue());
        wsProperties.store();
        final EditableProperties ep = new EditableProperties(true);
        final FileObject projPropsFO = prj.getProjectDirectory().getFileObject("nbproject/project.properties");

        try {
            final InputStream is = projPropsFO.getInputStream();
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {

                public Void run() throws Exception {
                    try {
                        ep.load(is);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    ep.setProperty(WebStartProjectProperties.JNLP_ENABLED, "true");
                    ep.setProperty(WebStartProjectProperties.JNLP_CBASE_URL, "");
                    OutputStream os = null;
                    FileLock lock = null;
                    try {
                        lock = projPropsFO.lock();
                        os = projPropsFO.getOutputStream(lock);
                        ep.store(os);
                    } finally {
                        if (lock != null) {
                            lock.releaseLock();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                    return null;
                }
            });
        } catch (MutexException mux) {
            throw (IOException) mux.getException();
        }
        File testFile = new File(File.separator+prj.getProjectDirectory().getFileObject("nbproject/project.properties").getPath());
        File passFile = new File(dataDir, "project.properties.pass");
        
        assertFile(testFile, passFile, dataDir);

        final ProjectConfigurationProvider configProvider = 
                    prj.getLookup().lookup(ProjectConfigurationProvider.class);
        assertNotNull(configProvider);
        wsProperties.createConfigurationFiles(configProvider, Boolean.valueOf(wsProperties.getProperty(WebStartProjectProperties.JNLP_ENABLED)).booleanValue());
        
        testFile = new File(File.separator+prj.getProjectDirectory().getFileObject("nbproject/jnlp-impl.xml").getPath());
        passFile = new File(dataDir, "jnlp-impl.xml.pass");
        assertFile(testFile, passFile, dataDir);
    }
    
}
