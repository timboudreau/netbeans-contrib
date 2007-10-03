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

package org.netbeans.modules.vcs.advanced.variables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.netbeans.modules.vcscore.VcsConfigVariable;

/**
 * Updater of conditioned values of variables. It assures that only changed
 * varibales will be updated.
 *
 * @author  Martin Entlicher
 */
public final class ConditionedVariablesUpdater extends Object {

    /** A map of variable names as keys and their last values as values. */
    private Map lastConditionValues = new HashMap();
    
    /** Creates a new instance of ConditionedVariablesUpdater */
    public ConditionedVariablesUpdater(ConditionedVariables cVars, Map lastVarValues) {
        initLastConditionValues(cVars, lastVarValues);
    }
    
    /**
     * Initialize the last conditioned values. This is necessary so that
     * {@link #updateConditionalValues()} does not reset variables that
     * did not change.
     */
    private void initLastConditionValues(ConditionedVariables cVars, Map lastVarValues) {
        Map conditionsByVariables = cVars.getConditionsByVariables();
        Map varsByConditions = cVars.getVariablesByConditions();
        if (conditionsByVariables.size() == 0) return ;
        for(Iterator it = conditionsByVariables.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Condition[] conditions = (Condition[]) conditionsByVariables.get(name);
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i].isSatisfied(lastVarValues)) {
                    VcsConfigVariable var = (VcsConfigVariable) varsByConditions.get(conditions[i]);
                    if (var != null) {
                        String value = (String) lastConditionValues.get(name);
                        if (!var.getValue().equals(value)) {
                            value = var.getValue();
                            lastConditionValues.put(name, value);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Conditional variables should be updated when the variables change.
     * However we must pay attention not to alter variables, that were set 
     * intentionally. Thus we should update
     * the variable values only when the condition result actually change.
     *
     * @param cVars The conditional variables as defined in the profile
     * @param varValues The map of actual variable values
     * @param varsByName An optional map of VcsConfigVariable objects that is
     *                   altered appropriatelly. Can be <code>null</code>
     * @param vars The vector of variables that will be altered appropriatelly
     * @return The vars Vector.
     */
    public Vector updateConditionalValues(ConditionedVariables cVars, Map varValues, Map varsByName, Vector variables) {
        Map conditionsByVariables = cVars.getConditionsByVariables();
        Map varsByConditions = cVars.getVariablesByConditions();
        if (conditionsByVariables.size() == 0) return variables;
        Map newVars = new HashMap();
        Set removedVars = new HashSet();
        for(Iterator it = conditionsByVariables.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Condition[] conditions = (Condition[]) conditionsByVariables.get(name);
            for (int i = 0; i < conditions.length; i++) {
                //System.out.println(" Condition: "+Condition.printCondition(conditions[i])+"; VAR = "+varsByConditions.get(conditions[i]));
                //System.out.println("  Conditioned var '"+conditions[i].getName()+"' "+(conditions[i].isSatisfied(varValues) ? "is" : "is not")+" satisfied");
                if (conditions[i].isSatisfied(varValues)) {
                    VcsConfigVariable var = (VcsConfigVariable) varsByConditions.get(conditions[i]);
                    //System.out.println("  Conditioned var '"+conditions[i].getName()+"' = "+(var == null ? "''" : "'"+var.getValue()+"'"));
                    if (var != null) {
                        String value = (String) lastConditionValues.get(name);
                        if (!var.getValue().equals(value)) {
                            newVars.put(name, var.clone());
                            value = var.getValue();
                            lastConditionValues.put(name, value);
                        }
                    } else removedVars.add(name);
                }
            }
        }
        for (int i = 0; i < variables.size(); i++) {
            VcsConfigVariable var = (VcsConfigVariable) variables.get(i);
            String name = var.getName();
            VcsConfigVariable newVar = (VcsConfigVariable) newVars.get(name);
            if (newVar != null) {
                variables.set(i, newVar);
                if (varsByName != null) varsByName.put(name, newVar);
            } else if (removedVars.contains(name)) {
                variables.remove(i);
            }
        }
        return variables;
    }
    
}
