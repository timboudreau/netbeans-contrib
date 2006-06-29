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
package org.netbeans.modules.paintcatcher;

import java.awt.Dialog;
import javax.swing.Action;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Action that can always be invoked and work procedurally.
 * @author tim
 */
public class ConfigureAction extends CallableSystemAction {
    static Class classToLog = null;
    static boolean logAWTEvents = true;
    static boolean matchIfAncestor = false;
    static boolean matchIfSubclass = false;
    static boolean dumpStack = false;
    static LogAction logAction = null;
    
    public ConfigureAction() {
    }
    
    public void performAction() {
        ConfigurationPanel panel = new ConfigurationPanel();
        
        panel.setLogAWTEvents(logAWTEvents);
        panel.setTargetClass(classToLog);
        panel.setMatchIfAncestor(matchIfAncestor);
        panel.setMatchSubclasses(matchIfSubclass);
        panel.setDumpStack(dumpStack);
        
        DialogDescriptor dd = new DialogDescriptor(panel, "Configure logging");
        
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.setModal(true);
        d.setVisible(true);
        if (true) {
            logAWTEvents = panel.isLogAWTEvents();
            matchIfAncestor = panel.isMatchIfAncestor();
            matchIfSubclass = panel.isMatchSubclasses();
            dumpStack = panel.isDumpStack();
            classToLog = panel.getTargetClass();
            if (classToLog == null) {
                StatusDisplayer.getDefault().setStatusText("Empty or unresolvable class");
            }
        }
        if (logAction != null) {
            logAction.setEnabled(classToLog != null);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ConfigureAction.class, "LBL_Config");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/paintcatcher/ConfigureActionIcon.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected void initialize() {
        super.initialize();
        putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ConfigureAction.class, "HINT_Config"));
    }
    
}
