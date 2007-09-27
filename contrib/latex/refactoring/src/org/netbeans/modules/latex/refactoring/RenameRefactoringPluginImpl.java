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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class RenameRefactoringPluginImpl implements RefactoringPlugin {

    private RenameRefactoring ref;
    
    public RenameRefactoringPluginImpl(RenameRefactoring ref) {
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
        final Data data = ref.getRefactoringSource().lookup(Data.class);
        Source source = data.getSource();
        final int caret = data.getCaret();
        final String replaceWith = ref.getNewName();
        try {
            source.runUserActionTask(new CancellableTask<CompilationController>() {
                public void cancel() {}
                public void run(CompilationController parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);
                    
                    LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                    Document doc = parameter.getDocument();
                    
                    if (doc == null)
                        return ;
                    
                    Object toSearch = ActionsImplementationProviderImpl.searchFor(doc, lpr, caret, new String[1]);
                    
                    if (toSearch instanceof Command) {
                        for (Node n : UsagesQuery.findCommandUsages(lpr, (Command) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n, data.getOriginalName(), replaceWith));
                        }
                    }
                    if (toSearch instanceof String) {
                        for (Node n : UsagesQuery.findLabelUsages(lpr, (String) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n, data.getOriginalName(), replaceWith));
                        }
                    }
                    if (toSearch instanceof Environment) {
                        for (Node n : UsagesQuery.findEnvironmentUsages(lpr, (Environment) toSearch)) {
                            refactoringElements.add(ref, new LaTeXRefactoringElementImplementation(n, data.getOriginalName(), replaceWith));
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
