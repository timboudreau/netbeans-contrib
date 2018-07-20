/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
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
