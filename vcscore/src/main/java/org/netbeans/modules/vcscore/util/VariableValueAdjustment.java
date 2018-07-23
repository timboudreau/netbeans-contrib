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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcscore.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This class is used to adjust variable values so that they can be expanded
 * to the execution string. Some shells needs some characters to be prepended
 * by an escape character to hide their special meaning.
 *
 * @author  Martin Entlicher
 */
public class VariableValueAdjustment implements Serializable {

    /** The variable name, that contains characters, that need to be prepended
     * by a special prefix character before execution.
     * The first character in this string is the prefix character.
     */
    private static final String VAR_ADJUST_CHARS = "ADJUST_CHARS";
    /** The variable names, whose values need to be adjusted before execution.
     * The {@link VAR_ADJUST_CHARS} defines the adjusting characters.
     */
    private static final String VAR_ADJUST_VARS = "ADJUST_VARS";

    private char adjustingChar;
    private HashSet adjustedChars = null;
    private HashSet adjustedVars = null;
    private String quoting = null;

    static final long serialVersionUID = 2773459026157834845L;
    /** Creates new VariableValueAdjustment */
    public VariableValueAdjustment() {
    }

    /** Adjusts "${" and "$[" strings so that they are not interpreted by the expansion method. */
    private static String adjustVarRef(String str) {
        str = org.openide.util.Utilities.replaceString(str, "${", "\\${"); // NOI18N
        str = org.openide.util.Utilities.replaceString(str, "$[", "\\$["); // NOI18N
        return str;
    }

    /** Revert the adjusted "\${" and "\$[" strings. */
    private static String revertAdjustedVarRef(String str) {
        str = org.openide.util.Utilities.replaceString(str, "\\${", "${"); // NOI18N
        str = org.openide.util.Utilities.replaceString(str, "\\$[", "$["); // NOI18N
        return str;
    }

    public synchronized void setAdjust(Map vars) {
        String adjustCharsStr = (String) vars.get(VAR_ADJUST_CHARS);
        if (adjustCharsStr == null) return ;
        this.adjustingChar = adjustCharsStr.charAt(0);
        this.adjustedChars = new HashSet();
        for (int i = 1; i < adjustCharsStr.length(); i++) {
            adjustedChars.add(new Character(adjustCharsStr.charAt(i)));
        }
        String adjustVarsStr = (String) vars.get(VAR_ADJUST_VARS);
        if (adjustVarsStr == null) return ;
        String[] adjustVars = VcsUtilities.getQuotedStrings(adjustVarsStr);
        this.adjustedVars = new HashSet(Arrays.asList(adjustVars));
        quoting = (String) vars.get(Variables.VAR_QUOTING);
        if (quoting != null && quoting.length() == 1 && adjustedChars.contains(new Character(quoting.charAt(0)))) {
            quoting = null; // The quoting is already excaped.
        }
    }

    /**
     * Get the set of variable names, that are adjusted.
     */
    public Set getAdjustedVariableNames() {
        if (adjustedVars == null) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.unmodifiableSet(adjustedVars);
        }
    }

    /**
     * Adjust the variable values for the command-line execution.
     * @param vars the map of variable names and their values
     */
    public void adjustVarValues(Map vars) {
        if (adjustedVars == null) return ;
        for (Iterator it = adjustedVars.iterator(); it.hasNext(); ) {
            String var = (String) it.next();
            String value = (String) vars.get(var);
            if (value != null) {
                vars.put(var, adjustVarValue(value));
            }
        }
    }

    /**
     * Adjust the variable value for the command-line execution.
     * @param value the value to be adjusted
     * @return the adjusted value
     */
    public String adjustVarValue(String value) {
        if (value == null) return value;
        value = adjustCharsInValue(value);
        if (quoting != null && value != null) {
            int index = 0;
            String adjustedQuoting = null;
            while ((index = value.indexOf(quoting, index)) >= 0) {
                if (adjustedQuoting == null) adjustedQuoting = adjustCharsInValue(quoting);
                value = value.substring(0, index) + adjustedQuoting + value.substring(index + quoting.length());
                index += adjustedQuoting.length();
            }
        }
        value = adjustVarRef(value);
        return value;
    }

    private String adjustCharsInValue(String value) {
        if (adjustedChars == null) return value;
        for (int i = 0; i < value.length(); i++) {
            Character c = new Character(value.charAt(i));
            if (adjustedChars.contains(c)) {
                value = value.substring(0, i) + adjustingChar + value.substring(i);
                i++;
            }
        }
        return value;
    }

    /**
     * Revert the adjusted variable values.
     * @param vars the map of variable names and their values
     */
    public void revertAdjustedVarValues(Map vars) {
        if (adjustedVars == null) return ;
        for (Iterator it = adjustedVars.iterator(); it.hasNext(); ) {
            String var = (String) it.next();
            String value = (String) vars.get(var);
            if (value != null) {
                vars.put(var, revertAdjustedVarValue(value));
            }
        }
    }

    /**
     * Revert the adjusted variable value.
     * @param value the value to be reverted
     * @return the reverted value
     */
    public String revertAdjustedVarValue(String value) {
        if (value == null) return value;
        value = revertAdjustedVarRef(value);
        value = revertAdjustedCharsInValue(value);
        if (quoting != null && value != null) {
            int index = 0;
            String revertedQuoting = null;
            while ((index = value.indexOf(quoting, index)) >= 0) {
                if (revertedQuoting == null) revertedQuoting = revertAdjustedCharsInValue(quoting);
                value = value.substring(0, index) + revertedQuoting + value.substring(index + quoting.length());
                index += revertedQuoting.length();
            }
        }
        return value;
    }

    private String revertAdjustedCharsInValue(String value) {
        if (adjustedChars == null) return value;
        int index = value.indexOf(adjustingChar);
        while(index >= 0 && index < (value.length() - 1)) {
            if (adjustedChars.contains(new Character(value.charAt(index + 1)))) {
                value = value.substring(0, index) + value.substring(index + 1);
            }
            index = value.indexOf(adjustingChar, index + 1);
        }
        return value;
    }

}
