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
package org.netbeans.modules.latex.refactoring.ui;

import java.awt.BorderLayout;
import org.netbeans.modules.latex.refactoring.FindUsagesPerformer;
import org.netbeans.modules.latex.refactoring.RefactorPerformer;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jan Lahoda
 */
public class UsagesTopComponent extends TopComponent {

    /** Creates a new instance of UsagesTopComponent */
    public UsagesTopComponent(FindUsagesPerformer performer, RefactorPerformer refactor, String name) {
        setLayout(new BorderLayout());
        
        UsagesPanel panel = new UsagesPanel(performer, refactor, name, this);
        
        add(panel, BorderLayout.CENTER);
        
        setDisplayName("Usages of " + name);
    }
    
    public void open() {
        findOutputMode().dockInto(this);
        super.open();
    }
    
    private Mode findOutputMode() {
        return WindowManager.getDefault().findMode("output");
    }

    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
}
