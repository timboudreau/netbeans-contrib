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
package org.netbeans.modules.java.additional.refactorings.splitclass;

import org.netbeans.modules.java.additional.refactorings.extractmethod.*;
import org.netbeans.modules.java.additional.refactorings.*;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class ChangeSignatureAction extends JavaRefactoringGlobalAction {

    public ChangeSignatureAction() {
        super (NbBundle.getMessage(ChangeSignatureAction.class,
                "LBL_CHANGE_SIG"), null); //NOI18N
        putValue (SHORT_DESCRIPTION, NbBundle.getMessage(ChangeSignatureAction.class,
                "DESC_CHANGE_SIG")); //NOI18N
    }

    public void performAction(final Lookup context) {
        EditorCookie ec = super.getTextComponent(context.lookup(Node.class));
        if (ec != null) {
            new TextComponentRunnable(ec) {
                @Override
                protected RefactoringUI createRefactoringUI(TreePathHandle selectedElement,int startOffset,int endOffset, CompilationInfo info) {
                    return new ChangeSignatureUI(context, selectedElement, info);
                }
            }.run();
        }
    }

    protected boolean enable(Lookup context) {
        return true;
    }
}
