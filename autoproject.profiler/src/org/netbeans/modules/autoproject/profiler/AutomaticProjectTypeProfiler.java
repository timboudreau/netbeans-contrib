/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.profiler;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.spi.AutomaticProjectMarker;
import org.netbeans.modules.profiler.AbstractProjectTypeProfiler;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 * Configures profiler to accept automatic projects.
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.profiler.spi.ProjectTypeProfiler.class)
public class AutomaticProjectTypeProfiler extends AbstractProjectTypeProfiler {

    /** public for lookup */
    public AutomaticProjectTypeProfiler() {}

    @Override
    public boolean isProfilingSupported(Project project) {
        return project.getLookup().lookup(AutomaticProjectMarker.class) != null;
    }

    @Override
    public boolean checkProjectIsModifiedForProfiler(Project project) {
        return true;
    }

    @Override
    public FileObject getProjectBuildScript(Project project) {
        return FileUtil.toFileObject(InstalledFileLocator.getDefault().locate(
                "autoproject-profile.xml", "org.netbeans.modules.autoproject.profiler", false));
    }

    @Override
    public String getProfilerTargetName(Project project, FileObject buildScript, int type, FileObject profiledClassFile) {
        // XXX do we need to pay attention to 'type' here?
        return "profile";
    }

    @Override
    public boolean isFileObjectSupported(Project project, FileObject fo) {
        return fo.hasExt("java");
    }

    @Override
    public boolean checkProjectCanBeProfiled(Project project, FileObject profiledClassFile) {
        return profiledClassFile != null &&
                ClassPath.getClassPath(profiledClassFile, ClassPath.SOURCE) != null &&
                ClassPath.getClassPath(profiledClassFile, ClassPath.EXECUTE) != null;
    }

    @Override
    public void configurePropertiesForProfiling(Properties props, Project project, FileObject profiledClassFile) {
        ClassPath sourcepath = ClassPath.getClassPath(profiledClassFile, ClassPath.SOURCE);
        String classname = sourcepath.getResourceName(profiledClassFile, '.', false);
        if (isTest(profiledClassFile, sourcepath)) {
            props.setProperty("classname", "junit.textui.TestRunner");
            props.setProperty("args", classname);
        } else {
            props.setProperty("classname", classname);
            props.setProperty("args", "");
        }
        props.setProperty("classpath", ClassPath.getClassPath(profiledClassFile, ClassPath.EXECUTE).toString(ClassPath.PathConversionMode.FAIL));
    }

    // XXX copied from ActionProviderImpl, should be moved into helper method
    private static boolean isTest(final FileObject fo, final ClassPath sourcepath) {
        final AtomicBoolean isActuallyTest = new AtomicBoolean();
        try {
            JavaSource.create(ClasspathInfo.create(fo)).runWhenScanFinished(new Task<CompilationController>() {
                public void run(CompilationController cc) throws Exception {
                    String name = sourcepath.getResourceName(fo, '.', false);
                    assert name != null : fo;
                    TypeElement runType = cc.getElements().getTypeElement(name);
                    assert runType != null : name;
                    TypeElement testCase = cc.getElements().getTypeElement("junit.framework.TestCase");
                    if (testCase != null && cc.getTypes().isAssignable(runType.asType(), testCase.asType())) {
                        isActuallyTest.set(true);
                    }
                }
            }, true).get();
        } catch (Exception x) {
            Logger.getLogger(AutomaticProjectTypeProfiler.class.getName()).log(Level.INFO, null, x);
        }
        return isActuallyTest.get();
    }

}
