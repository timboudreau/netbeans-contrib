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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class ToolbarFactory {
    
    private static final boolean debug = Boolean.getBoolean("netbeans.latex.toolbar.creation");
    
    /** Creates a new instance of ToolbarFactory */
    public ToolbarFactory() {
    }
    
    public static final Action getCommandAction(FileObject fo) {
        if (debug)
            System.err.println("getCommandAction start, fo=" + fo);
        
        Action action = new ToolbarCommandToggleAction((String) fo.getAttribute("actionIcon"), (String) fo.getAttribute("actionCommand"));//NOI18N
        
        action.putValue(Action.SHORT_DESCRIPTION, fo.getAttribute("actionTooltip"));//NOI18N
        
        return action;
    }

    public static final Action getEnvironmentAction(FileObject fo) {
        if (debug)
            System.err.println("getEnvironmentAction start, fo=" + fo);
        
        Action action = new ToolbarEnvironmentToggleAction((String) fo.getAttribute("actionIcon"), (String) fo.getAttribute("actionEnvironment"));//NOI18N
        
        action.putValue(Action.SHORT_DESCRIPTION, fo.getAttribute("actionTooltip"));//NOI18N
        
        return action;
    }

}
