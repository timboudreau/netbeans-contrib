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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The class to recognize makefile between other project files (source, text files, shell scripts etc.)
 * It parses the beginning of the file and finds includes, variable definitions, simple rules or
 *  incorrect statements (in that case the file is not a makefile)
 * @author Andrey Gubichev
 */
public class MakefileDetector {

    //symbols that are unlikely to be found in makefile
    private static final char[] InvalidSymbols = {'{', '}', '[', ']', ';'};
    //comment symbol in makefiles
    private static final String[] CommentSymbol = {"#"}; // NOI18N
    //symbols that are unlikely to be comment symbol in makefile
    private static final char[] InvalidCommentSymbol = {'*'};
    private static final String[] AssignmentSymbol = {"=", ":=", "+="}; // NOI18N
    private static String Include = "include"; // NOI18N
    //file that will be checked
    private File mkfile;

    /**
     * Creates a new instance of MakefileDetector
     * @param f file that will be checked
     */
    public MakefileDetector(File f) {
        mkfile = f;
    }

    //search for incorrect symbols in line s
    private static boolean hasValidStartSymbols(String s) {
        if (s.length() == 0) {
            return true;
        }
        char t = s.charAt(0);
        char c = s.charAt(s.length() - 1);
        for (int i = 0; i < InvalidCommentSymbol.length; i++) {
            if (t == InvalidCommentSymbol[i]) {
                return false;
            }
        }
        for (int i = 0; i < InvalidSymbols.length; i++) {
            if (t == InvalidSymbols[i] || c == InvalidSymbols[i]) {
                return false;
            }
        }
        return true;
    }

    //check if line s is a comment
    private static boolean isComment(String s) {
        s.trim();
        for (int i = 0; i < CommentSymbol.length; i++) {
            if (s.startsWith(CommentSymbol[i])) {
                return true;
            }
        }
        return false;
    }

    //check if line s is an include
    private static boolean isInclude(String s) {
        if (!s.startsWith(Include)) {
            return false;
        }
        String u = s.substring(Include.length(), s.length() - 1);
        u.trim();
        return !u.contains(" "); //there is more than one word after "include
    }

    //check if line s is a variable description
    private static boolean isDescription(String s) {
        int j = 0;
        for (int i = 0; i < AssignmentSymbol.length; i++) {
            j = s.indexOf(AssignmentSymbol[i]);
            if (j > 0 && j != s.length() - 1) {
                String u = s.substring(0, j - 1);
                if (u.indexOf(" ") != -1) { // NOI18N
                    //there is more than one word before '='
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    //check if line s has valid symbols
    private static boolean hasValidSymbols(String s) {
        if (isComment(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char t = s.charAt(i);
            for (int j = 0; j < InvalidSymbols.length; j++) {
                if (t == InvalidSymbols[j]) {
                    return false;
                }
            }
        }
        return true;
    }

    //the simplest check for target
    private static boolean isTarget(String s) {
        String t = s.trim();
        if (isComment(t) || !hasValidSymbols(t)) {
            return false;
        }
        return t.indexOf(":") > 0; // NOI18N
    }

    //the simplest check for rule
    private static boolean isRule(String first, String second) {
        if (second == null) {
            return false;
        }
        if (isTarget(first) && second.startsWith("    ")) { // NOI18N
            return true;
        }
        return false;
    }

    /**
     * check if file mkfile is a makefile
     * @return true if mkfile is a makefile
     */
    public boolean isMakefile() {
        boolean hasIncludes = false;
        boolean hasVar = false;
        boolean hasRules = false;
        boolean isMk = false;
        String s = "";
        String name = mkfile.getName().toLowerCase();
        if (name.contains("configure")) { // NOI18N
            return false;
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(mkfile.getPath()));
            s = in.readLine();
            s.trim();
            if (s.startsWith("#!")) { // NOI18N
                //it's a script
                return false;
            }
            while (s != null) {
                hasIncludes = false;
                hasVar = false;
                hasRules = false;
                s = s.trim();
                if (!hasValidStartSymbols(s)) {
                    break;
                }
                String t = in.readLine();
                if (s.length() == 0) {
                    s = t;
                    continue;
                }
                if (!isComment(s)) {
                    if (isInclude(s)) {
                        hasIncludes = true;
                    }
                    if (isDescription(s)) {
                        hasVar = true;
                    }
                    if (isRule(s, t)) {
                        hasRules = true;
                    }
                }
                if (hasIncludes || hasRules || hasVar) {
                    isMk = true;
                    break;
                }
                if (!hasIncludes && !hasRules && !hasVar && !isComment(s)) {
                    isMk = false;
                    break;
                }
                s = t;
            }
        } catch (IOException e) {
            return false;
        }
        return isMk;
    }
}