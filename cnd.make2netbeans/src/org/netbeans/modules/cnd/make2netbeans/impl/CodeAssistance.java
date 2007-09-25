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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    public static final String DOC = "info";
    /** Install Target */
    public static final String INSTALL = "install";
    /** Test Target */
    public static final String TEST = "test";
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
            System.out.println("-------------------------------");
            System.out.println("MAKEFILE PATH " + makefilePath);
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
            System.out.println("CodeAssistance.analyse : Unexpected Exception" + e);
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
        if (targetName.contains(".") || targetName.contains(File.separator) || targetName.contains("$")) {
            return MakefileTarget.Types.INTERMEDIATE;
        }
        if (targetName.equals("all")) {
            return MakefileTarget.Types.AGGREGATE;
        }
        if (targetName.contains("info") || targetName.contains("doc")) {
            return MakefileTarget.Types.DOCUMENTATION;
        }
        if (targetName.contains("install")) {
            return MakefileTarget.Types.INSTALL;
        }
        if (targetName.contains("clean")) {
            return MakefileTarget.Types.CLEAN;
        }
        if (targetName.contains("test")) {
            return MakefileTarget.Types.TEST;
        }
        return MakefileTarget.Types.INTERMEDIATE;
    }
}