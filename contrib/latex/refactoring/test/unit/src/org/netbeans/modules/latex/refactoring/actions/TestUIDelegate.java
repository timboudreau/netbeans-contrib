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
