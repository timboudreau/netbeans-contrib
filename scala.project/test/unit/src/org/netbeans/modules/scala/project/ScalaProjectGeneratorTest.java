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

package org.netbeans.modules.scala.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Tests for ScalaProjectGenerator.
 *
 * @author Martin Krauskopf
 */
public class ScalaProjectGeneratorTest extends NbTestCase {
    
    public ScalaProjectGeneratorTest(String testName) {
        super(testName);
    }
    
    private static final String[] createdFiles = {
        "build.xml",
        "nbproject/build-impl.xml",
        "nbproject/project.xml",
        //        "nbproject/project.properties",
        "src",
        //        "test",
    };
    
    private static final String[] createdProperties = {
        //        "build.classes.dir",
        //        "build.classes.excludes",
        //        "build.dir",
        //        "build.generated.dir",
        //        "build.sysclasspath",
        //        "build.test.classes.dir",
        //        "build.test.results.dir",
        //        "debug.classpath",
        //        "debug.test.classpath",
        //        "dist.dir",
        //        "dist.jar",
        //        "dist.javadoc.dir",
        //        "jar.compress",
        //        "javac.classpath",
        //        "javac.compilerargs",
        //        "javac.deprecation",
        //        "javac.source",
        //        "javac.target",
        //        "javac.test.classpath",
        //        "javadoc.author",
        //        "javadoc.encoding",
        //        "javadoc.noindex",
        //        "javadoc.nonavbar",
        //        "javadoc.notree",
        //        "javadoc.private",
        //        "javadoc.splitindex",
        //        "javadoc.use",
        //        "javadoc.version",
        //        "javadoc.windowtitle",
        //        "javadoc.additionalparam",
        //        "main.class",
        //        "manifest.file",
        //        "meta.inf.dir",
        //        "platform.active",
        //        "run.classpath",
        //        "run.jvmargs",
        //        "run.test.classpath",
        //        "test.src.dir",
    };
    
    protected void setUp() throws Exception {
        clearWorkDir();
    }
    
    public void testCreateProject() throws Exception {
        AntProjectHelper aph = ScalaProjectGenerator.createProject(
                new File(getWorkDir(), "testing-project"), "testing-project");
        assertNotNull(aph);
        FileObject fo = aph.getProjectDirectory();
        for (String file : createdFiles) {
            assertNotNull(file + " file/folder cannot be found", fo.getFileObject(file));
        }
        EditableProperties props = aph.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        List<String> l = new ArrayList<String>(props.keySet());
        for (String prop : createdProperties) {
            assertNotNull(prop + " property cannot be found in project.properties",
                    props.getProperty(prop));
            l.remove(prop);
        }
        assertEquals("Found unexpected property: " + l,
                createdProperties.length, props.keySet().size());
        
        
        FileObject build = fo.getFileObject("build.xml");

        try {
            AntSupport.execute(FileUtil.toFile(build));
            fail("There should be error:\n" + AntSupport.getStdOut());
        } catch (AntSupport.ExecutionError err) {
            if (err.getMessage().indexOf("Scala installation directory") == -1) {
                fail("There would be the scala warning:\n" + err.getMessage());
            }
        }
    }
    
}
