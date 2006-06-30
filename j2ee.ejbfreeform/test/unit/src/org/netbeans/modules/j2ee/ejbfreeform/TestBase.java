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
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.*;
import org.netbeans.modules.ant.freeform.FreeformProject;
import org.netbeans.modules.java.platform.JavaPlatformProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author jungi
 */
public class TestBase extends NbTestCase {
    
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
    }
    
    protected File egdir;
    protected FileObject buildXml;
    protected FreeformProject ejbFF;
    protected FileObject ejbJarXml;
    protected FileObject bean;
    protected FileObject schema;

    public TestBase(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        scratchF = getWorkDir();
        mkdir("system/J2EE/InstalledServers");
        mkdir("system/J2EE/DeploymentPlugins");
        System.setProperty("SYSTEMDIR", new File(scratchF, "system").getAbsolutePath());
        FileObject sfs = Repository.getDefault().getDefaultFileSystem().getRoot();
        assertNotNull("no default FS", sfs);
        FileObject j2eeFolder = sfs.getFileObject("J2EE");
        assertNotNull("have J2EE", j2eeFolder);
    }
    
    protected void setUpProject() throws Exception {
        egdir = FileUtil.normalizeFile(getDataDir());
        assertTrue("data dir " + egdir + " exists", egdir.exists());
        FileObject prjDir = FileUtil.toFileObject(egdir).getFileObject("test-app");
        assertNotNull("found projdir", prjDir);
        Project _ejbFF = ProjectManager.getDefault().findProject(prjDir);
        assertNotNull("have a project", _ejbFF);
        EJBProjectGeneratorTest.validate(_ejbFF);
        ejbFF = (FreeformProject)_ejbFF;
        ejbJarXml = prjDir.getFileObject("conf/ejb-jar.xml");
        assertNotNull("found ejb-jar.xml", ejbJarXml);
        bean = prjDir.getFileObject("src/beans/ent/CustomerLocal.java");
        assertNotNull("found CustomerLocal", bean);
        schema = prjDir.getFileObject("resources/APP_test-app.dbschema");
        assertNotNull("found dbschema", schema);
        buildXml = prjDir.getFileObject("build.xml");
        assertNotNull("found build.xml", buildXml);
    }

    protected boolean runInEQ() {
        return true;
    }
    
    private File scratchF;
    
    private void mkdir(String path) {
        new File(scratchF, path.replace('/', File.separatorChar)).mkdirs();
    }
    
    private static final class Repo extends Repository {
        
        public Repo() throws Exception {
            super(mksystem());
        }
        
        private static FileSystem mksystem() throws Exception {
            LocalFileSystem lfs = new LocalFileSystem();
            lfs.setRootDirectory(new File(System.getProperty("SYSTEMDIR")));
            //get layer for the generic server
            java.net.URL layerFile = Repo.class.getClassLoader().getResource("org/netbeans/modules/j2ee/genericserver/resources/layer.xml");
            assert layerFile != null;
            XMLFileSystem layer = new XMLFileSystem(layerFile);
            FileSystem layers [] = new FileSystem [] {lfs, layer};
            MultiFileSystem mfs = new MultiFileSystem(layers);
            return mfs;
        }
        
    }
    
    public static final class Lkp extends ProxyLookup {
        
        public Lkp() {
            super(new Lookup[] {
                Lookups.fixed(new Object[] {"repo", new DummyJavaPlatformProvider()}, new Conv()),
                Lookups.metaInfServices(Lkp.class.getClassLoader()),
                Lookups.singleton(Lkp.class.getClassLoader())
            });
        }
        
        private static final class Conv implements InstanceContent.Convertor {
            public Conv() {}
            public Object convert(Object obj) {
                if (obj instanceof JavaPlatformProvider) {
                    return obj;
                }
                assert obj == "repo";
                try {
                    return new Repo();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            public String displayName(Object obj) {
                return obj.toString();
            }
            public String id(Object obj) {
                return obj.toString();
            }
            public Class type(Object obj) {
                if (obj instanceof JavaPlatformProvider) {
                    return JavaPlatformProvider.class;
                }
                assert obj == "repo";
                return Repository.class;
            }
        }
    }

}
