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

package org.netbeans.modules.latex.refactoring;

import java.io.IOException;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationController;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.refactoring.actions.ActionsImplementationProviderImpl;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RenameRefactoring;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;
import org.netbeans.modules.refactoring.spi.ui.ActionsImplementationProvider;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class WhereUsedImpl implements RefactoringPlugin {

    private WhereUsedQuery ref;

    public WhereUsedImpl(WhereUsedQuery ref) {
        this.ref = ref;
    }
    
    public Problem preCheck() {
        return ref.getRefactoringSource().lookup(Data.class).getProblem();
    }

    public Problem checkParameters() {
        return ref.getRefactoringSource().lookup(Data.class).getProblem();
    }

    public Problem fastCheckParameters() {
        return ref.getRefactoringSource().lookup(Data.class).getProblem();
    }

    public void cancelRequest() {
    }

    public Problem prepare(final RefactoringElementsBag refactoringElements) {
        Source source = ref.getRefactoringSource().lookup(Data.class).getSource();
        final int caret = ref.getRefactoringSource().lookup(Data.class).getCaret();
        try {
            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                    Document doc = parameter.getDocument();
                    
                    if (doc == null)
                        return ;
                    
                    Object toSearch = ActionsImplementationProviderImpl.searchFor(doc, lpr, caret);
                    
                    if (toSearch instanceof Command) {
                        for (Node n : UsagesQuery.findCommandUsages(lpr, (Command) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n));
                        }
                    }
                    if (toSearch instanceof String) {
                        for (Node n : UsagesQuery.findLabelUsages(lpr, (String) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n));
                        }
                    }
                    if (toSearch instanceof Environment) {
                        for (Node n : UsagesQuery.findEnvironmentUsages(lpr, (Environment) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n));
                        }
                    }
                }
            }, true);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        
        return null;
    }

}
