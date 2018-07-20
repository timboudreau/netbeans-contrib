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

import java.util.LinkedList;
import java.util.Arrays;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * Parser for eval expressions og GNU make utility
 *
 *   File: Eval.java
 * Author: Arkady Galyash, SPbSU,   <my_another@mail.ru>
 *   Date: 12-Jul-2007
 */
class Eval {

    // all possible states
    private static final int DEFAULT = 0;
    private static final int VAR_OR_FUNC = 1;
    private static final int SKIP_WS = 2;
    // string has to be evaluated
    private String src;
    // number of readed chars
    private int index;
    // current state
    private int state;

    /* Constructor
     * @param String s string that have to be evaluated
     */
    public Eval(String s) {
        this.src = s;
    }

    /* This method should be called to evaluate all string src
     * @returns String result of evaluation
     */
    public String eval() {
        // starts in DEFAULT state
        state = DEFAULT;
        // starts with first char
        index = 0;
        // stops only when reads all string
        return eval(0); // 0 = 0*1 + 0*2 + 0*4
    }

    /*
     *  Methods for evaluating different types of arguments
     *  for builtin make functions
     */
    private String evalFirstArg() {
        // strts in SKIP_WS state to ignore leading whilespaces
        state = SKIP_WS;
        // when odd ')', '}' or ',' appears, stops
        return eval(5); // 5 = 1*1 + 0*2 + 1*4
    }

    private String evalMiddleArg() {
        // starts in DEFAULT state
        state = DEFAULT;
        // when odd ',' appears, stops
        return eval(1); // 4 = 1*1 + 0*2 + 0*4
    }

    private String evalLastArg() {
        // starts in DEFAULT state
        state = DEFAULT;
        // when odd ')', '}' appears, stops
        return eval(4); // 4 =  0*1 + 0*2 + 1*4
    }

    private String evalOnlyArg() {
        // strts in SKIP_WS state to ignore leading whilespaces
        state = SKIP_WS;
        // when odd ')', '}' appears, stops
        return eval(4); // 4 =  0*1 + 0*2 + 1*4
    }

    private String evalMiddleOrLastArg() {
        // starts in DEFAULT state
        state = DEFAULT;
        // when odd ')', '}' or ',' appears, stops
        return eval(5); // 5 = 1*1 + 0*2 + 1*4
    }

    private String evalPatternArg() {
        // starts in DEFAULT state
        state = DEFAULT;
        // when odd '=' appears, stops
        return eval(2); // 2 = 0*1 + 1*2 + 0*4
    }

    /*
     *  Methods for getting different types of arguments
     *  for builtin make functions
     *  (NO EVALUATING)
     */
    private String noevalLastArg() {
        return noeval(true);
    }

    private String noevalMiddleArg() {
        return noeval(false);
    }

    /* Evaluates needed part of String src
     * @param int mode contains bitmask which indicates should we
     *                 stop when odd chars appear
     *                 ------------------------------------|
     *                 | boolean var |  chars  |coeffitient|
     *                 |    name     |         |           |
     *                 ------------------------------------|
     *                 |    comma    |   ','   |     1     |
     *                 |   equals    |   '='   |     2     |
     *                 |   rBrace    | ')','}' |     4     |
     *                 -------------------------------------
     * @returns needed evaluating
     */
    private String eval(int mode) {

        // extract boolean values from bitmask 'mode'
        boolean comma = (mode % 2) == 1;
        boolean equals = ((mode % 4) / 2) == 1;
        boolean rBrace = ((mode % 8) / 4) == 1;

        // GNU makefile's could contain such structures as
        // '$(foo$(test))'. We could not find all name of variable
        //  foo$(test), before we read all this string.
        // So, internal representation is  "foo"->"test"
        LinkedList<StringBuilder> vars = new LinkedList<StringBuilder>();
        vars.addFirst(new StringBuilder());

        // until we don't reach end of string
        while (index < src.length()) {
            char c = src.charAt(index);
            index++;
            switch (state) {
                case DEFAULT:
                    {
                        // first char switch
                        switch (c) {
                            case '$':
                                {
                                    c = src.charAt(index);
                                    index++;
                                    // second char switch
                                    switch (c) {
                                    // you must write '$$' to have the effect of a
                                    // single dollar sign in a file name or command
                                        case '$':
                                            {
                                                vars.element().append('$');
                                                break;
                                            }
                                    // "$(" or "${" - begin of variable
                                    // or builtin function
                                        case '(':
                                        case '{':
                                            {
                                                vars.addFirst(new StringBuilder());
                                                state = VAR_OR_FUNC;
                                                break;
                                            }
                                    // A dollar sign followed by a character other
                                    // than a dollar sign, open-parenthesis or open-brace
                                    // treats that single character as the
                                    // variable name.
                                        default:
                                            {
                                                vars.element().append(Global.evalVar(c + ""));
                                                break;
                                            }
                                    } // second char switch
                                    break;
                                } // case '$'
                            case ')':
                            case '}':
                                {
                                    /* all var references are correct.
                                     * Number of "$(" and "${" are equal to number of
                                     * ")" and "}"
                                     * "$(foo$(test))" - correct
                                     * "$(foo$(test)" - not correct,
                                     *   ^
                                     *   |
                                     *  because this left-brace don't have
                                     *  corresponding right-brace
                                     */
                                    if (vars.size() == 1) {
                                        // odd '}' or ')' not allowed
                                        if (rBrace) {
                                            // so end reading
                                            return new String(vars.remove());
                                        }
                                    } else {
                                        state = VAR_OR_FUNC;
                                        index--;
                                        break;
                                    }
                                } // case ')', '}'
                            case ',':
                                {
                                    /* all var references are correct.
                                     * Number of "$(" and "${" are equal to number of
                                     * ")" and "}"
                                     */
                                    if (vars.size() == 1) {
                                        // odd ',' not allowed
                                        if (comma) {
                                            // so end reading
                                            return new String(vars.remove());
                                        }
                                    } else {
                                        vars.element().append(',');
                                        break;
                                    }
                                } // case ','
                            case '=':
                                {
                                    /* all var references are correct.
                                     * Number of "$(" and "${" are equal to number of
                                     * ")" and "}"
                                     */
                                    if (vars.size() == 1) {
                                        // odd '=' not allowed
                                        if (equals) {
                                            // so end reading
                                            return new String(vars.remove());
                                        }
                                    } else {
                                        vars.element().append('=');
                                        break;
                                    }
                                } // case '='
                            default:
                                // remember any other char
                                {
                                    vars.element().append(c);
                                    break;
                                }
                        } // first char switch
                        break;
                    } // case DEFAULT
                case VAR_OR_FUNC:
                    // reading name of variable or buildin make function
                    {
                        // char switch
                        switch (c) {
                            case ')':
                            case '}':
                                /* all var name was read, lets get value
                                 *  ...$(foo)...
                                 *          ^
                                 *          c
                                 */
                                {
                                    String value = Global.evalVar(new String(vars.remove()));
                                    vars.element().append(value);
                                    state = DEFAULT;
                                    break;
                                }
                            case ':':
                                /*  a simpler way to get the effect of the
                                 *  `patsubst' function:
                                 *   $(VAR:PATTERN=REPLACEMENT)
                                 *        ^
                                 *        c
                                 */
                                {
                                    // let get value of variable VAR
                                    String value = Global.evalVar(new String(vars.remove()));
                                    // read PATTERN
                                    String[] pattern = pattern(evalPatternArg());
                                    String[] replacement = new String[3];

                                    /* The second shorthand simplifies one of the
                                     * most common uses of `patsubst': replacing the suffix
                                     * at the end of file names.
                                     *
                                     * $(VAR:SUFFIX=REPLACEMENT)
                                     * is equivalent to
                                     * $(patsubst %SUFFIX,%REPLACEMENT,$(VAR))
                                     */
                                    if (pattern[1] == "") {
                                        pattern[2] = pattern[0];
                                        pattern[0] = "";
                                        pattern[1] = "%"; // NOI18N
                                        replacement[0] = "";
                                        replacement[1] = "%"; // NOI18N
                                        replacement[2] = evalPatternArg();
                                    } else {
                                        // read REPLACEMENT
                                        replacement = pattern(evalLastArg());
                                    }
                                    // get value of patsubst builtin function
                                    vars.element().append(patsubst(pattern, replacement, value));
                                    break;
                                } // case ':'
                            case ' ':
                            case '\t':
                                /* ...$(xxxx ...
                                 *          ^
                                 *          c
                                 * if xxxx - name one of builtin make functions
                                 * then it is function call, not variable reference
                                 */
                                {
                                    String func = vars.element().toString();
                                    if (func.equals("subst")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String from = evalFirstArg();
                                        String to = evalMiddleArg();
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(subst(from, to, text));
                                    } else if (func.equals("patsubst")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String[] pattern = pattern(evalFirstArg());
                                        String[] replacement = pattern(evalMiddleArg());
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(patsubst(pattern, replacement, text));
                                    } else if (func.equals("strip")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String string = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(strip(string));
                                    } else if (func.equals("findstring")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String find = evalFirstArg();
                                        String in = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(findstring(find, in));
                                    } else if (func.equals("filter")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String[] patterns = evalFirstArg().trim().split("( |\t)+", 0); // NOI18N
                                        LinkedList<String[]> list = new LinkedList<String[]>();
                                        for (int i = 0; i < patterns.length; i++) {
                                            list.add(pattern(patterns[i]));
                                        }
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(filter(list, text));
                                    } else if (func.equals("filter-out")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String[] patterns = evalFirstArg().trim().split("( |\t)+", 0); // NOI18N
                                        LinkedList<String[]> list = new LinkedList<String[]>();
                                        for (int i = 0; i < patterns.length; i++) {
                                            list.add(pattern(patterns[i]));
                                        }
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(filter_out(list, text));
                                    } else if (func.equals("sort")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String list = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(sort(list));
                                    } else if (func.equals("dir")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String names = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(dir(names));
                                    } else if (func.equals("notdir")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String names = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(notdir(names));
                                    } else if (func.equals("suffix")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String names = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(suffix(names));
                                    } else if (func.equals("basename")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String names = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(basename(names));
                                    } else if (func.equals("addsuffix")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String suffix = evalFirstArg();
                                        String names = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(addsuffix(suffix, names));
                                    } else if (func.equals("addprefix")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String prefix = evalFirstArg();
                                        String names = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(addprefix(prefix, names));
                                    } else if (func.equals("join")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String list1 = evalFirstArg();
                                        String list2 = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(join(list1, list2));
                                    } else if (func.equals("word")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String n = evalFirstArg();
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(word(n, text));
                                    } else if (func.equals("wordlist")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String s = evalFirstArg();
                                        String e = evalMiddleArg();
                                        String text = evalLastArg();
                                        // get value of builtin function
                                        vars.element().append(wordlist(s, e, text));
                                    } else if (func.equals("words")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String text = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(words(text));
                                    } else if (func.equals("firstword")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String names = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(firstword(names));
                                    } else if (func.equals("wildcard")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String pattern = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(wildcard(pattern));
                                    } else if (func.equals("foreach")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String var = evalFirstArg();
                                        String list = evalMiddleArg();
                                        String text = noevalLastArg();
                                        // get value of builtin function
                                        vars.element().append(foreach(var, list, text));
                                    } else if (func.equals("if")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String result;
                                        String condition = evalFirstArg();
                                        if (condition.equals("")) {
                                            noevalMiddleArg();
                                            result = evalLastArg();
                                        } else {
                                            result = evalMiddleArg();
                                            noevalLastArg();
                                        }
                                        // get value of builtin function
                                        vars.element().append(result);
                                    } else if (func.equals("call")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        LinkedList<String> params = new LinkedList<String>();
                                        String variable = noevalMiddleArg();
                                        // for reading unknown number of arguments
                                        while (src.charAt(index - 1) == ',') {
                                            params.add(evalMiddleOrLastArg());
                                        }
                                        // get value of builtin function
                                        vars.element().append(call(variable, params));
                                    } else if (func.equals("shell")) { // NOI18N
                                        // delete function name
                                        vars.remove();
                                        String command = evalOnlyArg();
                                        // get value of builtin function
                                        vars.element().append(shell(command));
                                    }
                                    break;
                                } // case ' ', '\t'
                            case '$':
                                /* This is var reference inside another var name such
                                 * $(foo$(test))"
                                 *      ^
                                 *      c
                                 * We do the same what we do in DEFAULT state, so lets
                                 * only change the state
                                 */
                                {
                                    state = DEFAULT;
                                    index--;
                                    break;
                                } // case '$'
                            default:
                                {
                                    vars.element().append(c);
                                    break;
                                }
                        } // char switch
                        break;
                    } // case VAAR_OR_FUNC
                case SKIP_WS:
                    // ignore leading whitespaces
                    {
                        switch (c) {
                            case ' ':
                            case '\t':
                                break;
                            default:
                                {
                                    index--;
                                    state = DEFAULT;
                                    break;
                                }
                        }
                    } // case SKIP_WS
            } // switch(state)
        } // while (index < src.length())
        return new String(vars.remove());
    }

    /* Makefile pattern may contain a `%' which acts as a wildcard,
     * matching any number of any characters within a word.
     * `%' characters can be quoted with preceding backslashes (`\').
     * Backslashes that would otherwise quote `%' characters can be quoted
     * with more backslashes. Backslashes that are not in danger of
     * quoting `%' characters go unmolested.  For example, the pattern
     * `the\%weird\\%pattern\\' has `the%weird\' preceding the operative
     * `%' character, and `pattern\\' following it.  The final two
     * backslashes are left alone because they cannot affect any `%'
     * character.
     * @returns String[3]: String[0] contains substring before `%'
     *                     String[1] conatins `%'(if `%' appears)
     *                     String[2] contains substring after `%'
     * @param String src GNU make pattern
     */
    private String[] pattern(String src) {
        String[] result = new String[3];
        int index = src.indexOf('%');
        switch (index) {
            case -1:
                // there are no any `%' in string src
                {
                    // remove double baskslashes
                    result[0] = src.replaceAll("\\\\", "\\"); // NOI18N
                    result[1] = "";
                    result[2] = "";
                    return result;
                }
            case 0:
                // src=%....
                // src starts with `%'
                {
                    result[0] = "";
                    result[1] = "%"; // NOI18N
                    result[2] = src.substring(1);
                    return result;
                }
            default:
                // `%' in the middle of string src
                {
                    while (index != -1) {
                        // this `%' is quoted
                        if (src.charAt(index - 1) == '\\') {
                            index = index + 1;
                        } else {
                            result[0] = src.substring(0, index).replaceAll("\\\\", "\\"); // NOI18N
                            result[1] = "%"; // NOI18N
                            result[2] = src.substring(index + 1);
                            return result;
                        }
                        // find next `%'
                        index = src.indexOf('%', index);
                    } // while (index != -1)
                } // default
        } // switch(index)
        result[0] = src.replaceAll("\\\\", "\\").replaceAll("\\%", "%"); // NOI18N
        result[1] = "";
        result[2] = "";
        return result;
    }

    /* Reads needed part of String src
     * @param boolean lastArg should we stop when odd `,' appear
     *                        if lastArg-true, then we shouldn't
     * @returns needed aprt of string
     */
    private String noeval(boolean lastArg) {
        StringBuilder result = new StringBuilder();
        // counts difference between number of "${" ("$(") and "}" (")")
        int depth = 1;
        // until we don't reach end of string
        while (index < src.length()) {
            char c = src.charAt(index);
            index++;
            switch (c) {
                case '$':
                    {
                        c = src.charAt(index);
                        index++;
                        switch (c) {
                            case '(':
                            case '{':
                                {
                                    depth++;
                                }
                            default:
                                {
                                    result.append("$" + c); // NOI18N
                                    break;
                                }
                        }
                        break;
                    } // case '$'
                case ')':
                case '}':
                    {
                        depth--;
                        // there are one more ")" then "$("
                        if (depth == 0) {
                            return result.toString();
                        }
                        result.append(c);
                        break;
                    }
                case ',':
                    {
                        if ((!(lastArg)) && (depth == 1)) {
                            return result.toString();
                        }
                        result.append(c);
                        break;
                    }
                default:
                    {
                        result.append(c);
                        break;
                    }
            } // switch(c)
        } // while (index < src.length())
        return result.toString();
    }
    //--------------------------------------------------------
    // Functions for String Substitution and Analysis
    // See 'info make Functions Text Functions' for details
    //--------------------------------------------------------

    private String subst(String from, String to, String text) {
        return text.replace(from, to);
    }

    private String patsubst(String[] pattern, String[] replacement, String text) {
        String[] words;
        String result = new String("");
        if (pattern[1].equals("%")) { // NOI18N
            words = text.trim().split("( |\t)+", 0); // NOI18N
        } else {
            return text.replaceAll("\\B" + pattern[0] + "\\B", replacement[0] + replacement[1] + replacement[2]); // NOI18N
        }
        for (int i = 0; i < words.length; i++) {
            if (words[i].startsWith(pattern[0]) && words[i].endsWith(pattern[2])) {
                result += replacement[0];
                if (replacement[1].equals("%")) { // NOI18N
                    result += words[i].substring(pattern[0].length(), words[i].length() - pattern[2].length());
                }
                result += replacement[2];
            } else {
                result += words[i];
            }
            result += " "; // NOI18N
        }
        return strip(result);
    }

    private String strip(String string) {
        try {
            return string.trim().replaceAll("( |\t)+", " "); // NOI18N
        } catch (Exception e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.strip():\n" + e);
            return new String("");
        }
    }

    private String findstring(String find, String in) {
        if (in.contains(find)) {
            return find;
        }
        return new String("");
    }

    private String filter(LinkedList<String[]> patterns, String text) {
        String result = new String("");
        String[] words = text.trim().split("( |\t)+", 0); // NOI18N
        for (int i = 0; i < words.length; i = i + 1) {
            for (int j = 0; j < patterns.size(); j++) {
                if (words[i].startsWith(patterns.get(j)[0]) && words[i].endsWith(patterns.get(j)[2]) && (words[i].length() == patterns.get(j)[0].length() + patterns.get(j)[2].length() || patterns.get(j)[1] == "%")) { // NOI18N
                    result += words[i] + " "; // NOI18N
                    break;
                }
            }
        }
        if (result.length() < 1) {
            return "";
        }
        return result.substring(0, result.length() - 1);
    }

    private String filter_out(LinkedList<String[]> patterns, String text) {
        String result = new String("");
        String[] words = text.trim().split("( |\t)+", 0); // NOI18N
        for (int i = 0; i < words.length; i = i + 1) {
            boolean filter = true;
            for (int j = 0; j < patterns.size(); j++) {
                if (words[i].startsWith(patterns.get(j)[0]) && words[i].endsWith(patterns.get(j)[2]) && (words[i].length() == patterns.get(j)[0].length() + patterns.get(j)[2].length() || patterns.get(j)[1] == "%")) { // NOI18N
                    filter = false;
                    break;
                }
            }
            if (filter) {
                result += words[i] + " "; // NOI18N
            }
        }
        if (result.length() < 1) {
            return "";
        }
        return result.trim();
    }

    private String sort(String list) {
        String[] words = list.trim().split("( |\t)+", 0); // NOI18N
        if (words.length == 0) {
            return "";
        }
        if (words.length == 1) {
            return words[0];
        }
        Arrays.sort(words);
        String prev = words[0];
        String result = words[0] + " "; // NOI18N
        for (int i = 1; i < words.length; i++) {
            if (!prev.equals(words[i])) {
                result += words[i] + " "; // NOI18N
                prev = words[i];
            }
        }
        return result.substring(0, result.length() - 1);
    }
    //--------------------------------------------------------
    // Functions for File Names
    // See 'info make Functions File Name Functions' for details
    //--------------------------------------------------------

    private String dir(String names) {
        String[] words = names.trim().split("( |\t)+", 0); // NOI18N
        String result = "";
        for (int i = 0; i < words.length; i++) {
            int index = words[i].lastIndexOf('/');
            String tmp = "";
            if (index == words[i].length()) {
                tmp = words[i];
            } else if (index == -1) {
                tmp = "./"; // NOI18N
            } else {
                tmp = words[i].substring(0, index + 1);
            }
            result += tmp + " "; // NOI18N
        }
        return result.substring(0, result.length() - 1);
    }

    private String notdir(String names) {
        String[] words = names.trim().split("( |\t)+", 0); // NOI18N
        String result = new String("");
        for (int i = 0; i < words.length; i++) {
            int index = words[i].lastIndexOf('/');
            String tmp = new String("");
            if (index == words[i].length()) {
                tmp = "";
            } else if (index == -1) {
                tmp = words[i] + " "; // NOI18N
            } else {
                tmp = words[i].substring(index + 1, words[i].length()) + " "; // NOI18N
            }
            result += tmp;
        }
        return result.substring(0, result.length() - 1);
    }

    private String suffix(String names) {
        String[] words = names.trim().split("( |\t)+", 0); // NOI18N
        String result = "";
        for (int i = 0; i < words.length; i++) {
            int slash = words[i].lastIndexOf('/');
            int dot = words[i].lastIndexOf('.');
            if (dot > slash) {
                result += words[i].substring(dot, words[i].length()) + " "; // NOI18N
            }
        }
        return result.substring(0, result.length() - 1);
    }

    private String basename(String names) {
        String[] words = names.trim().split("( |\t)+", 0); // NOI18N
        String result = "";
        for (int i = 0; i < words.length; i++) {
            int slash = words[i].lastIndexOf('/');
            int dot = words[i].lastIndexOf('.');
            if (dot > slash) {
                result += words[i].substring(0, dot) + " "; // NOI18N
            } else {
                result += words[i] + " "; // NOI18N
            }
        }
        return result.substring(0, result.length() - 1);
    }

    private String addsuffix(String suffix, String names) {
        try {
            return strip(names).replaceAll(" ", suffix + " ") + suffix; // NOI18N
        } catch (java.lang.NullPointerException e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.addsuffix():\n" + e);
            return new String("");
        }
    }

    private String addprefix(String prefix, String names) {
        try {
            return prefix + strip(names).replaceAll(" ", " " + prefix); // NOI18N
        } catch (java.lang.NullPointerException e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.addprefix():\n" + e);
            return new String("");
        }
    }

    private String join(String list1, String list2) {
        String[] words1 = list1.trim().split("( |\t)+", 0); // NOI18N
        String[] words2 = list2.trim().split("( |\t)+", 0); // NOI18N
        String[] min = (words1.length > words2.length) ? words2 : words1;
        String[] max = (words1.length < words2.length) ? words2 : words1;
        String result = "";
        for (int i = 0; i < min.length; i++) {
            result += words1[i] + words2[i] + " "; // NOI18N
        }
        for (int i = min.length; i < max.length; i++) {
            result += max[i] + " "; // NOI18N
        }
        return result.substring(0, result.length() - 1);
    }

    private String word(String n, String text) {
        try {
            Integer integer = Integer.parseInt(n.trim());
            String[] words = text.trim().split("( |\t)+", 0); // NOI18N
            if (integer > words.length) {
                return "";
            }
            return words[integer - 1];
        } catch (Exception e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.word():\n" + e);
            return "";
        }
    }

    private String wordlist(String s, String e, String text) {
        try {
            Integer begin = Integer.parseInt(s.trim());
            Integer end = Integer.parseInt(e.trim());
            // starts with 1, not 0
            begin--;
            end--;
            if (begin > end) {
                return "";
            }
            String[] words = text.trim().split("( |\t)+", 0); // NOI18N
            if (begin > words.length) {
                return "";
            }
            String result = "";
            for (int i = begin; (i < words.length) && (i <= end); i++) {
                result += words[i] + " "; // NOI18N
            }
            return result.substring(0, result.length() - 1);
        } catch (Exception ex) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.wordlist():\n" + ex);
            return "";
        }
    }

    private String words(String text) {
        String[] words = text.trim().split("( |\t)+", 0); // NOI18N
        return String.valueOf(words.length);
    }

    private String firstword(String text) {
        String[] words = text.trim().split("( |\t)+", 0); // NOI18N
        return words[0];
    }

    // FIXME It needs 'shell' and 'ls' FIXME
    private String wildcard(String pattern) {
        try {
            String[] arr = new String[3];
            arr[0] = "sh"; // NOI18N
            arr[1] = "-c"; // NOI18N
            arr[2] = "ls " + pattern; // NOI18N
            Process child = Runtime.getRuntime().exec(arr);
            BufferedReader in = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String result = new String();
            String tmp;
            while ((tmp = in.readLine()) != null) {
                result += " " + tmp; // NOI18N
            }
            return result.trim();
        } catch (Exception e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.wildcard():\n" + e);
            return "";
        }
    }
    //--------------------------------------------------------
    // The 'foreach' Function
    // See 'info make Functions Foreach Function' for details
    //--------------------------------------------------------

    private String foreach(String var, String list, String text) {
        String result = new String("");
        String[] words = list.trim().split("( |\t)+", 0); // NOI18N
        for (int i = 0; i < words.length; i++) {
            Global.localVars.put(var, words[i]);
            result += Global.eval(text) + " "; // NOI18N
            Global.localVars = new TreeMap<String, String>();
        }
        if (result.length() < 1) {
            return "";
        }
        return result.substring(0, result.length() - 1);
    }
    //--------------------------------------------------------
    // The 'call' Function
    // See 'info make Functions Call Function' for details
    //--------------------------------------------------------

    private String call(String var, LinkedList<String> params) {
        String value = Global.globalVars.get(var);
        for (int i = 0; i < params.size(); i++) {
            Global.localVars.put(i + 1 + "", params.get(i));
        }
        value = Global.eval(value);
        Global.localVars = new TreeMap<String, String>();
        return value;
    }
    //--------------------------------------------------------
    // The 'shell' Function
    // See 'info make Functions Shell Function' for details
    //--------------------------------------------------------

    // FIXME It needs 'shell'  FIXME
    private String shell(String cmd) {
        try {
            String[] arr = new String[3];
            arr[0] = "sh"; // NOI18N
            arr[1] = "-c"; // NOI18N
            arr[2] = cmd;
            Process child = Runtime.getRuntime().exec(arr);
            BufferedReader in = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String result = new String();
            String tmp;
            while ((tmp = in.readLine()) != null) {
                result += " " + tmp; // NOI18N
            }
            if (child.waitFor() != 0) {
                return "";
            }
            return result.trim();
        } catch (Exception e) {
            System.err.println("evalfunc.g: unexpected Exception in " + "EvalFuncLexer.shell():\n" + e);
            return "";
        }
    }
}