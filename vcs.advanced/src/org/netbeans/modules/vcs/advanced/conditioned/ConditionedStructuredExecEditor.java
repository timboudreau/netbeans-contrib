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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExecEditor extends PropertyEditorSupport {
    
    private ConditionedString cexecString;
    private ConditionedObject cexecStructured;
    private VcsCommand cmd;
    private Map cproperties;
    
    /** Creates a new instance of StructuredExecEditor *
    public ConditionedStructuredExecEditor() {
        this(new ConditionedString("exec", new HashMap()));
    }
     */
    
    public ConditionedStructuredExecEditor(ConditionedString cexecString, VcsCommand cmd, Map cproperties) {
        this.cexecString = cexecString;
        this.cmd = cmd;
        this.cproperties = cproperties;
    }
    
    public String getAsText() {
        Map vbc;
        if (cexecStructured == null || ((vbc = cexecStructured.getValuesByConditions()).size() == 1 && vbc.values().iterator().next() == null)) {
            return cexecString.toString();
        } else {
            return cexecStructured.toString();
        }
    }
    
    public java.awt.Component getCustomEditor() {
        ConditionedStructuredExecPanel panel = new ConditionedStructuredExecPanel(cmd, cproperties);
        panel.setExecStringConditioned(cexecString);
        panel.setExecStructuredConditioned(cexecStructured);
        return panel;
    }
    
    public Object getValue() {
        //cmd.setProperty(VcsCommand.PROPERTY_EXEC, execString);
        return cexecStructured;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        // Unimplemented
    }
    
    public void setValue(Object value) {
        cexecStructured = (ConditionedObject) value;
        ConditionedProperty cproperty = (ConditionedProperty) cproperties.get(VcsCommand.PROPERTY_EXEC);
        Map valuesByConditions = cexecString.getValuesByConditions();
        ConditionedProperty newCProperty;
        Object varValue = null;
        if (valuesByConditions.size() == 1 && valuesByConditions.keySet().iterator().next() == null) {
            newCProperty = null;
            varValue = valuesByConditions.get(null);
        } else {
            if (cproperty != null) {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, cproperty.getCondition(), valuesByConditions);
            } else {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, null, valuesByConditions);
            }
        }
        if (newCProperty != null) {
            cproperties.put(VcsCommand.PROPERTY_EXEC, newCProperty);
        } else {
            cproperties.remove(VcsCommand.PROPERTY_EXEC);
            cmd.setProperty(VcsCommand.PROPERTY_EXEC, varValue);
        }
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}
