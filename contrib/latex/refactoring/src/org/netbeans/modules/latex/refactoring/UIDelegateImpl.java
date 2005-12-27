/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.refactoring;

import org.netbeans.modules.latex.refactoring.ui.UsagesTopComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class UIDelegateImpl extends UIDelegate {
    
    /** Creates a new instance of UIDelegateImpl */
    public UIDelegateImpl() {
    }

    public String getNewName(String oldName) {
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Rename To:", "Rename");
        
        nd.setInputText(oldName);
        
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            return nd.getInputText();
        }
        
        return null;
    }

    public void showRefactoring(FindUsagesPerformer performer, RefactorPerformer refactor, String name) {
        TopComponent tc = new UsagesTopComponent(performer, refactor, name);
        
        tc.open();
        tc.requestActive();
    }
    
}
