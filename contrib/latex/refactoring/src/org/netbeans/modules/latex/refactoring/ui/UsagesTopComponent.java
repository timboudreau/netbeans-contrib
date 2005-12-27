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
