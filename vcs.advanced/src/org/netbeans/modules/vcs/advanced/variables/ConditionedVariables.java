/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.netbeans.modules.vcscore.VcsConfigVariable;

/**
 * This class provides methods for maintaining variables with respect to conditions.
 *
 * @author  Martin Entlicher
 */
public final class ConditionedVariables extends Object {
    
    /** Collection of variables, that are not affected by any condition. */
    private Collection unconditionedVars;
    /** An array of conditions, that are defined for variables.
     * The keys are just variable names, values are Condition[].
     * The condition contains var items with empty value and equals compare value.
     * <br>
     * Thus <code>if="testVar1" unless="testVar2"</code> maps to condition:<br>
     * <code>
     * &lt;and&gt;<br>
     * &nbsp;&nbsp;&lt;not&gt; &lt;var name="testVar1" value=""/&gt; &lt;/not&gt;<br>
     * &nbsp;&nbsp;&lt;var name="testVar2" value=""/&gt;<br>
     * &lt;/and&gt;<br>
     * </code>
     */
    private Map conditionsByVariables;
    /** Variables defined when the condition is true. */
    private Map varsByConditions;
    
    /** Creates a new instance of ConditionedVariables.
     * @param unconditionedVars The collection of variables, that are not affected by any condition.<br>
     * @param conditionsByVariables A map of An array of conditions, that are defined for variables.
     * The keys are just variable names, values are Condition[].
     * The condition contains var items with empty value and equals compare value.
     * <br>
     * Thus <code>if="testVar1" unless="testVar2"</code> maps to condition:<br>
     * <code>
     * &lt;and&gt;<br>
     * &nbsp;&nbsp;&lt;not&gt; &lt;var name="testVar1" value=""/&gt; &lt;/not&gt;<br>
     * &nbsp;&nbsp;&lt;var name="testVar2" value=""/&gt;<br>
     * &lt;/and&gt;<br>
     * </code>
     * @param varsByConditions The variables defined when the condition is true.
     */
    public ConditionedVariables(Collection unconditionedVars,
                                Map conditionsByVariables,
                                Map varsByConditions) {
        this.unconditionedVars = unconditionedVars;
        this.conditionsByVariables = conditionsByVariables;
        this.varsByConditions = varsByConditions;
    }
    
    /**
     * Get the collection of VcsConfigVariable objects, that are defined
     * for the provided map of conditional variables.
     * @param conditionalVars Map of conditional variable names and their values.
     * @return The collection of VcsConfigVariable objects.
     */
    public Collection getVariables(Map conditionalVars) {
        Collection vars = new ArrayList(unconditionedVars);
        for(Iterator it = conditionsByVariables.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Condition[] conditions = (Condition[]) conditionsByVariables.get(name);
            for (int i = 0; i < conditions.length; i++) {
                if (conditions[i].isSatisfied(conditionalVars)) {
                    VcsConfigVariable var = (VcsConfigVariable) varsByConditions.get(conditions[i]);
                    if (var != null) vars.add(var);
                }
            }
        }
        return vars;
    }
    
    /**
     * Get the map of variable names and their values, that are defined
     * for the provided map of conditional variables.
     * @param conditionalVars Map of conditional variable names and their values.
     * @return The map of variable names and their values.
     */
    public Map getVariableMap(Map conditionedVars) {
        return createVariableMap(getVariables(conditionedVars));
    }
    
    /**
     * Get the collection of variables (VcsConfigVariable objects), that are
     * defined when a list of conditions is evaluated and a map of default
     * variables is taken into account. The defined variables are also included
     * in the conditions.
     */
    public Collection getSelfConditionedVariables(Condition[] conditions,
                                                  Map defaultConditionedVars) {
        Map allVarsMap = new HashMap(defaultConditionedVars);
        addVarConditions(conditions, allVarsMap, null);
        Map variableMap = getVariableMap(allVarsMap);
        allVarsMap.putAll(variableMap);
        Collection conditionsVars = new HashSet();
        addVarConditions(conditions, allVarsMap, conditionsVars);
        Collection variables = getVariables(allVarsMap);
        variables.addAll(conditionsVars);
        return variables;
    }
    
    /**
     * Get the map of variable names and their values, that are
     * defined when a list of conditions is evaluated and a map of default
     * variables is taken into account. The defined variables are also included
     * in the conditions.
     */
    public Map getSelfConditionedVariableMap(Condition[] conditions,
                                             Map defaultConditionedVars) {
        return createVariableMap(getSelfConditionedVariables(conditions, defaultConditionedVars));
    }
    
    private static Map createVariableMap(Collection variables) {
        Map map = new java.util.HashMap();
        for (Iterator it = variables.iterator(); it.hasNext(); ) {
            VcsConfigVariable var = (VcsConfigVariable) it.next();
            map.put(var.getName(), var.getValue());
        }
        return map;
    }
    
    private static void addVarConditions(Condition[] conditions, Map varsMap, Collection vars) {
        if (conditions != null) {
            for (int i = 0; i < conditions.length; i++) {
                String name = conditions[i].getName();
                if (conditions[i].isSatisfied(varsMap)) {
                    varsMap.put(name, Boolean.TRUE.toString());
                    if (vars != null) {
                        vars.add(new VcsConfigVariable(name, null, Boolean.TRUE.toString(), false, false, false, null));
                    }
                } else {
                    varsMap.remove(name);
                }
            }
        }
    }
    
    /**
     * Get collection of variables, that are not affected by any condition.
     */
    public Collection getUnconditionedVariables() {
        return unconditionedVars;
    }
    
    /**
     * Get the map of variables defined when the condition is true.
     */
    public Map getVariablesByConditions() {
        return varsByConditions;
    }
    
    /**
     * Get an array of conditions, that are defined for variables.
     * The keys are just variable names, values are Condition[].
     * The condition contains var items with empty value and equals compare value.
     * <br>
     * Thus <code>if="testVar1" unless="testVar2"</code> maps to condition:<br>
     * <code>
     * &lt;and&gt;<br>
     * &nbsp;&nbsp;&lt;not&gt; &lt;var name="testVar1" value=""/&gt; &lt;/not&gt;<br>
     * &nbsp;&nbsp;&lt;var name="testVar2" value=""/&gt;<br>
     * &lt;/and&gt;<br>
     * </code>
     * <br>
     * Variable, that has a main condition <code>cm</code> next to <code>name</code>
     * attribute and <code>n</code> values with conditions <code>cv_1, ..., cv_n</code>
     * have following conditions for values:<br>
     * <code>
     * C_i = var condition for cv_i<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;+ sub condition for cm<br>
     * </code>
     * If <code>value_j</code> is without a condition, following condition is constructed:<br>
     * <code>
     * C_j = sub condition for cm<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;+ { sub condition for !C_i; i != j }
     * </code>
     */
    public Map getConditionsByVariables() {
        return conditionsByVariables;
    }
    
    /*
    public void addVariable(VcsConfigVariable var, String[] positiveConditions,
                            String[] negativeConditions) {
    }
    
    public void removeVariable(VcsConfigVariable var, String[] positiveConditions,
                               String[] negativeConditions) {
    }
     */
    
}
