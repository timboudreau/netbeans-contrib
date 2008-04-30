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

package org.netbeans.modules.cnd.profiler.providers;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author eu155513
 */
public class GprofProvider implements ProfilerProvider {
    private final Project project;
    
    private static final String PROFILING_FOLDER_NAME = "profiling";

    public GprofProvider(Project project) {
        this.project = project;
    }

    public void prepare() {
        // recompile project with -pg flag
    }

    public void run() {
        // just run the project
        ActionProvider ap = project.getLookup().lookup(ActionProvider.class);
        if (ap == null) {
            return; // fail early
        }
        ap.invokeAction("run", Lookup.EMPTY);
        
        // 3) wait for completion and prepare/open gprof results
        FileObject projectDir = project.getProjectDirectory();
        try {
            // create profiling folder if needed
            FileObject profilingDir = projectDir.getFileObject(PROFILING_FOLDER_NAME);
            if (profilingDir == null) {
                profilingDir = projectDir.createFolder(PROFILING_FOLDER_NAME);
            }
            
            // execute gprof on gmon.out
            FileObject gmon = projectDir.getFileObject("gmon.out");
            if (gmon == null) {
                return;
            }
            Runtime rt = Runtime.getRuntime();
            try {
                FileObject resFile = profilingDir.createData(String.valueOf(System.currentTimeMillis()));
                Process proc = rt.exec("ggprof -b " + gmon.getPath() + " > " + resFile.getPath());
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void cancel() {
        // cancel run
    }
}
