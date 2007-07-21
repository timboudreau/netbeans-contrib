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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.refactoring.actions;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.modules.latex.refactoring.Data;
import org.netbeans.modules.latex.refactoring.ui.NewNamePanel;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXFURefactoringUI implements RefactoringUI {

    private Source s;
    private int caret;
    private String name;
    private String newName;
    private Problem problem;
    private boolean whereUsed;

    public LaTeXFURefactoringUI(Source s, int caret, String name, Problem problem, boolean whereUsed) {
        this.s = s;
        this.caret = caret;
        this.name = name;
        this.problem = problem;
        this.whereUsed = whereUsed;
    }
    
    public String getName() {
        return whereUsed ? "Find Usages" : "Rename";
    }

    public String getDescription() {
        return whereUsed ? "Find Usages" : "Rename";
    }

    public boolean isQuery() {
        return whereUsed;
    }

    private NewNamePanel panel;
    
    public CustomRefactoringPanel getPanel(ChangeListener parent) {
        if (whereUsed) {
            return new CustomRefactoringPanel() {
                public void initialize() {
                }
                public Component getComponent() {
                    return new JPanel();
                }
            };
        } else {
            return new CustomRefactoringPanel() {
                public void initialize() {
                }

                public Component getComponent() {
                    if (panel == null) {
                        panel = new NewNamePanel();
                    }
                    return panel;
                }
            };
        }
    }

    public Problem setParameters() {
        newName = panel.getNewName();
        return null;
    }

    public Problem checkParameters() {
        return null;
    }

    public boolean hasParameters() {
        return !whereUsed;
    }

    public AbstractRefactoring getRefactoring() {
        if (whereUsed) {
            return new WhereUsedQuery(Lookups.fixed(new Data(s, caret, name, problem)));
        } else {
            RenameRefactoring rr = new RenameRefactoring(Lookups.fixed(new Data(s, caret, name, problem, newName)));
            
            rr.setNewName(newName);
            
            return rr;
        }
    }

    public HelpCtx getHelpCtx() {
        return null;
    }

}
