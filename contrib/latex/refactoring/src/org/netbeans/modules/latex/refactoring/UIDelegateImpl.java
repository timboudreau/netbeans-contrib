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
