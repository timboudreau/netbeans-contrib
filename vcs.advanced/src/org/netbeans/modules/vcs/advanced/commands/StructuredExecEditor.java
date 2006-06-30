/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
    private boolean writable;

    /** Creates a new instance of StructuredExecEditor */
    public StructuredExecEditor(VcsCommand cmd, boolean writable) {
        this.cmd = cmd;
        this.execString = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        this.writable = writable;
    }
    
    public String getAsText() {
        if (execStructured == null) {
            return execString;
        } else {
            return execStructured.toString();
        }
    }
    
    public java.awt.Component getCustomEditor() {
        StructuredExecPanel panel = new StructuredExecPanel(cmd,writable);
        panel.setExecString(execString);
        panel.setExecStructured(execStructured);
        return panel;
    }
    
    public Object getValue() {
        cmd.setProperty(VcsCommand.PROPERTY_EXEC, execString);
        return execStructured;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        if (execStructured == null) {
            execString = text;
        }
    }
    
    public void setValue(Object value) {
        execStructured = (StructuredExec) value;
        execString = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}
