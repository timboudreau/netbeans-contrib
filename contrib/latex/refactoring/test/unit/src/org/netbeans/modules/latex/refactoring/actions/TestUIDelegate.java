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
package org.netbeans.modules.latex.refactoring.actions;

import org.netbeans.modules.latex.refactoring.FindUsagesPerformer;
import org.netbeans.modules.latex.refactoring.RefactorPerformer;
import org.netbeans.modules.latex.refactoring.UIDelegate;

/**
 *
 * @author Jan Lahoda
 */
public class TestUIDelegate extends UIDelegate {
    
    public String oldName;
    public String newName;
    public FindUsagesPerformer performer;
    public RefactorPerformer refactor;
    public String showName;
    
    public int getNewNameCount;
    public int showRefactoringCount;
    
    /** Creates a new instance of TestUIDelegate */
    public TestUIDelegate() {
    }

    public String getNewName(String oldName) {
        this.oldName = oldName;
        
        return this.newName;
    }

    public void showRefactoring(FindUsagesPerformer performer, RefactorPerformer refactor, String name) {
        this.performer = performer;
        this.refactor = refactor;
        this.showName = name;
    }
    
    public void clear() {
        this.oldName = null;
        this.newName = null;
        this.performer = null;
        this.refactor = null;
        this.showName = null;
        this.getNewNameCount = 0;
        this.showRefactoringCount = 0;
    }
    
}
