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
package org.netbeans.modules.java.additional.refactorings;

import java.io.IOException;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.openide.filesystems.FileObject;

/**
 * Copy of RetoucheRefactoringPlugin from refactoring/java
 */
public abstract class Refactoring extends JavaRefactoringPlugin implements CancellableTask<CompilationController>  {

    public Refactoring() {
    }

    private enum Switch {PRECHECK, FASTCHECKPARAMETERS, CHECKPARAMETERS, PREPARE, DEFAULT};
    private Switch whatRun = Switch.DEFAULT;
    private Problem problem;

    protected abstract Problem preCheck(CompilationController javac) throws IOException;
    protected abstract Problem checkParameters(CompilationController javac) throws IOException;
    protected abstract Problem fastCheckParameters(CompilationController javac) throws IOException;
    protected abstract Problem prepare(WorkingCopy wc, RefactoringElementsBag bag) throws IOException;

    protected abstract FileObject getFileObject();

    public void cancel() {
    }

    public final void run(CompilationController javac) throws Exception {
        switch(whatRun) {
        case PRECHECK:
            this.problem = preCheck(javac);
            break;
        case CHECKPARAMETERS:
            this.problem = checkParameters(javac);
            break;
        case FASTCHECKPARAMETERS:
            this.problem = fastCheckParameters(javac);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    public Problem preCheck() {
        return run(Switch.PRECHECK);
    }

    public Problem checkParameters() {
        return run(Switch.CHECKPARAMETERS);
    }

    public Problem fastCheckParameters() {
        return run(Switch.FASTCHECKPARAMETERS);
    }

    public Problem prepare(final RefactoringElementsBag bag) {
        this.whatRun = Switch.PREPARE;
        this.problem = null;
        FileObject fo = getFileObject();
        JavaSource js = JavaSource.forFileObject(fo);
        try {
            js.runModificationTask(new CancellableTask<WorkingCopy>() {
                public void cancel() {
                }

                public void run(WorkingCopy wc) throws Exception {
                    prepare(wc, bag);
                }
            }).commit();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return problem;
    }

    private Problem run(Switch s) {
        this.whatRun = s;
        this.problem = null;
        FileObject fo = getFileObject();
        JavaSource js = JavaSource.forFileObject(fo);
        try {
            js.runUserActionTask(this, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return problem;
    }
}
