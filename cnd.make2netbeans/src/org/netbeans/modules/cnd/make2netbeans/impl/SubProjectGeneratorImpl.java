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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.make2netbeans.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.cnd.make2netbeans.api.SubProjectGenerator;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 * The class to generate subprojects.
 * The class creates main project and all its subprojects.
 * @author Andrey Gubichev
 */
public class SubProjectGeneratorImpl implements SubProjectGenerator {

    private String workingDir;
    private String buildCommand;
    private String cleanCommand;
    private String output;
    private String makefilePath;
    private String projectFolder;
    private String makefileName;
    private File makefile;
    private File dir;
    private String prefixName;
    private int depthLevel;
    private boolean invokeDwarf;

    /** Creates a new instance of SubProjectGenerator */
    public SubProjectGeneratorImpl() {
    }

    /**
     * Generate all subprojects according
     * to list of subprojects that creates
     * DivideProject object
     * @throws java.io.IOException in case something went wrong
     */
    public void generate() throws IOException {
        generate(dir, 0, prefixName);
    }

    private AntProjectHelper generate(File f, int level, String prefix) throws IOException {
        if (level > depthLevel && depthLevel>=0) {
            return null;
        }
        DividerImpl d = new DividerImpl();
        AntProjectHelper parent = generateProject(f, d, prefix, invokeDwarf &&(level==depthLevel));
        if (d.canBeDivided()) {
            if (prefix.length()>0) {
                prefix +="."+f.getName(); // NOI18N
            }
            level++;
            List<File> subpr = d.getSubprojects();
            for (int i = 0; i < subpr.size(); i++) {
                d = new DividerImpl();
                AntProjectHelper child = generate(subpr.get(i), level, prefix);
                //       AntProjectHelper child = generateProject((File)subpr.get(i),d);
                //setReference(parent, child);
            }
        }
        return parent;
    }

    private AntProjectHelper generateProject(File f, DividerImpl d, String prefix, boolean runDiscovery) throws IOException {
        ProjectCreator creator = new ProjectCreator();
        String t = IpeUtils.toRelativePath(workingDir, f.getAbsolutePath());
        String path = projectFolder + File.separator + t;
        File g = new File(path);
        g.mkdirs();
        String projectName = f.getName();
        String newWorkingDir = f.getAbsolutePath();
        makefilePath = newWorkingDir + File.separator + makefileName;
        creator = new ProjectCreator();
        creator.init(path, newWorkingDir, makefilePath);
        creator.setBuildCommand(buildCommand);
        creator.setCleanCommand(cleanCommand);
        File subMakefile = new File(makefilePath);

        creator.setOutput(output);
        d.init(subMakefile);
        d.canBeDivided();
        List<File> flist = d.getFiles();
        creator.setSourceFiles(flist);
        if (!runDiscovery && d.canBeDivided()) {
            List<String> subpr = new ArrayList<String>();
            for (File file : d.getSubprojects()) {
                subpr.add(file.getName());
            }
            creator.setRequiredProjects(subpr);
        }
        String displayName;
        if (prefix.length()>0){
            displayName = prefix+"."+projectName; // NOI18N
        } else {
            displayName = projectName;
        }
        AntProjectHelper h = creator.createProject(projectName, displayName, runDiscovery);
        return h;
    }

    /**
     * initialize
     * @param newProjectFolder project folder
     * @param newWorkingDir working directory (for build and clean commands)
     * @param newMakefilePath path to existing makefile
     */
    public void init(String newProjectFolder, String newWorkingDir, String newMakefilePath) {
        workingDir = newWorkingDir;
        projectFolder = newProjectFolder;
        makefilePath = newMakefilePath;
        makefile = new File(makefilePath);
        makefileName = makefile.getName();
        dir = new File(workingDir);
    }

    /**
     *
     * @param cmd new build command
     */
    public void setBuildCommand(String cmd) {
        buildCommand = cmd;
    }

    /**
     *
     * @param cmd new clean command
     */
    public void setCleanCommand(String cmd) {
        cleanCommand = cmd;
    }

    /**
     *
     * @param out new output
     */
    public void setOutput(String out) {
        output = out;
    }

    public void setPrefixName(String prefix) {
        prefixName = prefix;
    }

    public void setDepthLevel(int depth) {
        depthLevel = depth;
    }

    public void setInvokeDwarfProvider(boolean dwarf) {
        invokeDwarf = dwarf;
    }
}