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

package org.netbeans.modules.vcs.advanced.commands;

import java.beans.PropertyEditorSupport;

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/**
 *
 * @author  Martin Entlicher
 */
public class StructuredExecEditor extends PropertyEditorSupport {
    
    private VcsCommand cmd;
    private String execString;
    private StructuredExec execStructured;
    
    /** Creates a new instance of StructuredExecEditor */
    public StructuredExecEditor(VcsCommand cmd) {
        this.cmd = cmd;
        this.execString = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
    }
    
    public String getAsText() {
        if (execStructured == null) {
            return execString;
        } else {
            return execStructured.toString();
        }
    }
    
    public java.awt.Component getCustomEditor() {
        StructuredExecPanel panel = new StructuredExecPanel();
        panel.setExecString(execString);
        panel.setExecStructured(execStructured);
        return panel;
    }
    
    public Object getValue() {
        cmd.setProperty(VcsCommand.PROPERTY_EXEC, execString);
        return execStructured;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        // Unimplemented
    }
    
    public void setValue(Object value) {
        execStructured = (StructuredExec) value;
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}
