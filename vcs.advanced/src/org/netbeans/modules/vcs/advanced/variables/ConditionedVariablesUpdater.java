/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
