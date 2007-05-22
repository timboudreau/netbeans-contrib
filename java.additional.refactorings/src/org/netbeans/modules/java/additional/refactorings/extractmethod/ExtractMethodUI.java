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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import java.util.Collections;
import javax.lang.model.element.Modifier;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class ExtractMethodUI implements RefactoringUI {
    private final Lookup lkp;
    public ExtractMethodUI(Lookup lkp, TreePathHandle selectedElement,int startOffset,int endOffset, CompilationInfo info) {
        this.lkp = lkp;
        this.handle = selectedElement;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public String getName() {
        return "Extract Method";        
    }

    public String getDescription() {
        return "Extracts a Method";        
    }

    public boolean isQuery() {
        return false;
    }

    ExtractMethodPanel panel = new ExtractMethodPanel(this);
    ChangeListener listener = null;
    public CustomRefactoringPanel getPanel(ChangeListener parent) {
        listener = parent;
        return panel;
    }
    
    void change() {
        listener.stateChanged (new ChangeEvent (this));
    }

    public Problem setParameters() {
        return panel == null || panel.getProblemText() != null 
                ? new Problem (true, "Method name not set") : null;
    }

    public Problem checkParameters() {
        return setParameters();
    }

    public boolean hasParameters() {
        return true;
    }

    public AbstractRefactoring getRefactoring() {
        return new ExtractMethodRefactoring(lkp, handle, startOffset, endOffset,
                panel == null ? "newMethod" : panel.getMethodName(), panel == null ? Collections.<Modifier>emptySet() :
                    panel.getModifiers());
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    private int endOffset;
    private int startOffset;
    private TreePathHandle handle;

}
