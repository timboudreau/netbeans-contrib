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

import java.util.TreeMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import antlr.LexerSharedInputState;
import java.util.LinkedList;
import java.util.ArrayList;

/*
 * Contains all global variables
 *
 *    File: Global.java
 *  Author: Arkady Galyash, SPbSU,   <my_another@mail.ru>
 *    Date: 27-Mar-2007
 */
public abstract class Global {

    // tree with key - variable name
    //         value - variable value
    public static TreeMap<String, String> globalVars = new TreeMap<String, String>();
    // tree with key - variable name
    //         value - variable value
    public static TreeMap<String, String> localVars = new TreeMap<String, String>();
    // all defines (`-D' option for `gcc')
    public static HashMap<String, String> macros = new HashMap<String, String>();
    // all include directories
    public static HashSet<String> includeDirs = new HashSet<String>();
    // several Lexers for `include' directive handling
    public static LinkedList<LexerSharedInputState> makefiles = new LinkedList<LexerSharedInputState>();
    // all targets from current makefile
    public static ArrayList<String> targets = new ArrayList<String>();
    // path to makefile
    public static File pwd;
    // for EvalVarLexer
    public static boolean done;

    /*
     * clear all fields.
     * Must be invoked before start analyze new Makefile
     */
    public static void clear() {
        globalVars = new TreeMap<String, String>();
        includeDirs = new HashSet<String>();
        macros = new HashMap<String, String>();
        makefiles = new LinkedList<LexerSharedInputState>();
        done = false;
    }

    /*
     * Evaluates string(handling variables anf\d builtin functions)
     * @param input String to be evaluated
     * @return evaluated string
     */
    public static String eval(String input) {
        try {
            /*
            EvalVarLexer lexer = new EvalVarLexer(new StringReader(input));
            done = false;
            while(!done)
            {
            // when hit EOF var `done' would be changed
            lexer.nextToken();
            }
            EvalFuncLexer lex = new EvalFuncLexer(
            new StringReader(lexer.getResult()));
            // all string is always only one token
            lex.nextToken();
            return lex.getResult();
             */
            Eval e = new Eval(input);
            String tmp = e.eval();
            return tmp;
        } catch (Exception e) {
            System.err.println("Global.java: unexpected Exception in " + "Global.eval():\n" + e);
            return new String("");
        }
    }

    /*
     * Evaluates known variables
     * @param varName variable name to be evaluated
     * @return evaluated string
     */
    public static String evalVar(String varName) {
        // unknown variable stays as is
        if (!Global.globalVars.containsKey(varName)) {
            if (!Global.localVars.containsKey(varName)) {
                return new String("$(" + varName + ")"); // NOI18N
            } else {
                return eval(Global.localVars.get(varName));
            }
        }
        return eval(Global.globalVars.get(varName));
    }

    /*
     * Evaluates all include paths
     */
    public static void evalIncludeDirs() {
        Iterator<String> it = includeDirs.iterator();
        HashSet<String> newIncludeDirs = new HashSet<String>();
        while (it.hasNext()) {
            String dir = eval(it.next());
            it.remove();
            try {
                // the path is not absolute
                File directory = new File(pwd, dir);
                if (directory.isDirectory()) {
                    newIncludeDirs.add(directory.getAbsolutePath());
                } else {
                    // the path is absolute
                    directory = new File(dir);
                    if (directory.isDirectory()) {
                        newIncludeDirs.add(directory.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                System.err.println("Global.evalIncludeDirs() Exception " + e);
            }
        }
        includeDirs = newIncludeDirs;
    }

    /*
     * Evaluates define macroses
     */
    public static String evalMacros() {
        StringBuilder defines = new StringBuilder();

        Iterator<Map.Entry<String, String>> it = macros.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> item = it.next();
            String name = eval(item.getKey());
            String value = eval(item.getValue());
            if (value != null && value.startsWith("\\\"") && value.endsWith("\\\"")&& value.length()>=4){ // NOI18N
                value = "\""+value.substring(2,value.length()-2)+"\""; // NOI18N
            } else if (value != null && value.startsWith("'\"") && value.endsWith("\"'")&& value.length()>=4){ // NOI18N
                value = "\""+value.substring(2,value.length()-2)+"\""; // NOI18N
            }
            defines.append(eval(item.getKey()) + "=" + eval(item.getValue() + " ")); // NOI18N
        }
        return defines.toString();
    }
    
}