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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.cnd.make2netbeans.api.CodeAssistanceInterface;
import org.netbeans.modules.cnd.make2netbeans.api.MakefileTarget;
import org.netbeans.modules.cnd.make2netbeans.impl.generated.MakefileLexer;
import org.netbeans.modules.cnd.make2netbeans.impl.generated.MakefileParser;

/**
 * The class to find include paths and targets of makefile.
 * @author Arkady Galyash
 */
public class CodeAssistance implements CodeAssistanceInterface {

    private boolean isAnalysed;
    private boolean doneTargets;
    /** Doc Target */
    public static final String DOC = "info"; // NOI18N
    /** Install Target */
    public static final String INSTALL = "install"; // NOI18N
    /** Test Target */
    public static final String TEST = "test"; // NOI18N
    private ArrayList<MakefileTarget> targets;
    private String installTarget;
    private String docTarget;
    private String testTarget;

    /** Creates a new instance of CodeAssistance */
    public CodeAssistance() {
        isAnalysed = false;
        doneTargets = false;
        installTarget = null;
        docTarget = null;
        testTarget = null;
    }

    //actually analyse makefile
    private void analyse(String makefilePath) {
        isAnalysed = true;
        Global.clear();
        try {
            Global.pwd = new File(makefilePath);
            System.out.println("-------------------------------"); // NOI18N
            System.out.println("MAKEFILE PATH " + makefilePath); // NOI18N
            Global.pwd = new File(Global.pwd.getParentFile(), File.separator);
            MakefileLexer lexer = new MakefileLexer(new FileReader(makefilePath));
            MakefileParser parser = new MakefileParser(lexer);
            Global.makefiles.addFirst(lexer.getInputState());
            while (Global.makefiles.size() > 0) {
                Global.makefiles.remove();
                try {
                    parser.makefile();
                } catch (IncludeMakefileException e) {
                    Global.makefiles.addLast(lexer.getInputState());
                    lexer.setInputState(Global.makefiles.getFirst());
                    //}catch(antlr.TokenStreamException e){
                    //    if (e.getMessage() != "INCLUDE MAKEFILE"){
                    //        throw new java.io.IOException();
                    //    }
                }
            }
        } catch (Exception e) {
            System.out.println("CodeAssistance.analyse : Unexpected Exception" + e); // NOI18N
            e.printStackTrace();
        }
    }

    /**
     *
     * @param makefilePath path to existing makefile
     * @return list of include directories
     */
    public List<String> getIncludes(String makefilePath) {
        if (!isAnalysed) {
            analyse(makefilePath);
        }
        Global.evalIncludeDirs();
        return new ArrayList<String>(Global.includeDirs);
    }

    /**
     *
     * @param makefilePath path to existing makefile
     * @return list of makefile targets
     */
    public List<MakefileTarget> getTargets(String makefilePath) {
        doneTargets = true;
        if (!isAnalysed) {
            analyse(makefilePath);
        }
        ArrayList<String> targ = Global.targets;
        targets = new ArrayList<MakefileTarget>();
        for (int i = 0; i < targ.size(); i++) {
            String name = targ.get(i);
            if (name.equals(DOC)) {
                docTarget = name;
            } else if (name.equals(INSTALL)) {
                installTarget = name;
            } else if (name.equals(TEST)) {
                testTarget = name;
            }
            MakefileTarget.Types type = getTargetType(name);
            MakefileTarget target = new MakefileTarget(name, type);

            targets.add(target);
        }
        return targets;
    }

    /**
     *
     * @param makefilePath path to existing makefile
     * @return name of the end target of "Install" configuration
     */
    public String getInstallTarget(String makefilePath) {
        if (!doneTargets) {
            getTargets(makefilePath);
        }
        return installTarget;
    }

    /**
     *
     * @param makefilePath path to existing makefile
     * @return name of the end target of Documentation configuration
     */
    public String getDocTarget(String makefilePath) {
        if (!doneTargets) {
            getTargets(makefilePath);
        }
        return docTarget;
    }

    /**
     *
     * @param makefilePath path to existing makefile
     * @return end target of Test configuration
     */
    public String getTestTarget(String makefilePath) {
        if (!doneTargets) {
            getTargets(makefilePath);
        }
        return testTarget;
    }

    //get the type of specified target (Aggregate, Documentation, Install, Clean, Test or Intermediate
    private MakefileTarget.Types getTargetType(String targetName) {
        if (targetName.contains(".") || targetName.contains(File.separator) || targetName.contains("$")) { // NOI18N
            return MakefileTarget.Types.INTERMEDIATE;
        }
        if (targetName.equals("all")) { // NOI18N
            return MakefileTarget.Types.AGGREGATE;
        }
        if (targetName.contains("info") || targetName.contains("doc")) { // NOI18N
            return MakefileTarget.Types.DOCUMENTATION;
        }
        if (targetName.contains("install")) { // NOI18N
            return MakefileTarget.Types.INSTALL;
        }
        if (targetName.contains("clean")) { // NOI18N
            return MakefileTarget.Types.CLEAN;
        }
        if (targetName.contains("test")) { // NOI18N
            return MakefileTarget.Types.TEST;
        }
        return MakefileTarget.Types.INTERMEDIATE;
    }
}