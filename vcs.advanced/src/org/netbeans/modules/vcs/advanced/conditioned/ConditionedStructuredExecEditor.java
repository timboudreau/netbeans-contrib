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

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExecEditor extends PropertyEditorSupport {
    
    private ConditionedString cexecString;
    private ConditionedObject cexecStructured;
    
    /** Creates a new instance of StructuredExecEditor */
    public ConditionedStructuredExecEditor() {
        this(new ConditionedString("exec", new HashMap()));
    }
    
    public ConditionedStructuredExecEditor(ConditionedString cexecString) {
        this.cexecString = cexecString;
    }
    
    public String getAsText() {
        if (cexecStructured == null) {
            return cexecString.toString();
        } else {
            return cexecStructured.toString();
        }
    }
    
    public java.awt.Component getCustomEditor() {
        ConditionedStructuredExecPanel panel = new ConditionedStructuredExecPanel();
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
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}
