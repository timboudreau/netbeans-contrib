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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import org.netbeans.modules.java.additional.refactorings.visitors.ParameterRenamePolicy;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignaturePanel;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeMirror;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.modules.java.additional.refactorings.Utils;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tim Boudreau
 */
public class ChangeSignatureUI implements RefactoringUI, CancellableTask <CompilationController> {
    private TreePathHandle handle;
    private final Lookup lkp;
    public ChangeSignatureUI(Lookup lkp, TreePathHandle selectedElement, CompilationInfo info) {
        this.lkp = lkp;
        this.handle = selectedElement;
    }

    public String getName() {
        return NbBundle.getMessage (ChangeSignatureUI.class,
                "LBL_CHANGE_SIG_UI"); //NOI18N
    }

    public String getDescription() {
        return NbBundle.getMessage (ChangeSignatureUI.class,
                "DESC_CHANGE_SIG_UI"); //NOI18N
    }

    public boolean isQuery() {
        return false;
    }

    ChangeSignaturePanel panel = new ChangeSignaturePanel(this);
    ChangeListener listener = null;
    public CustomRefactoringPanel getPanel(ChangeListener parent) {
        listener = parent;
        return panel;
    }
    
    void change() {
        listener.stateChanged (new ChangeEvent (this));
    }

    private Problem problem;
    public Problem setParameters() {
        if (panel == null) return null;
        return problem != null ? problem : 
            panel.getProblemText() != null 
                ? new Problem (true, panel.getProblemText()) : null;
    }

    public Problem checkParameters() {
        return setParameters();
    }

    public boolean hasParameters() {
        return true;
    }

    public AbstractRefactoring getRefactoring() {
        List <Parameter> originals = panel.originals;
        List <Parameter> now = panel.getNewParameters();
        String returnType = panel.getReturnType();
        String methodName = panel.getMethodName();
        ParameterRenamePolicy policy = panel.getRenamePolicy();
        boolean refactorFromBase = panel.isRefactorFromBase();
        return new ChangeSignatureRefactoring (handle, lkp, originals, now, 
                methodName, returnType, policy, refactorFromBase);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    void init() {
        final FileObject fob = handle.getFileObject();
        final JavaSource src = JavaSource.forFileObject(fob);
        RequestProcessor.getDefault().post (new Runnable() {
           public void run() {
               try {
                   src.runUserActionTask(ChangeSignatureUI.this, true);
               } catch (IOException ioe) {
                   Exceptions.printStackTrace(ioe);
               }
           } 
        });
    }
    
    private void setProblem (String problem) {
        this.problem = new Problem (true, problem);
        change();
    }

    volatile boolean cancelled = false;
    public void cancel() {
        cancelled = true;
    }

    public void run(CompilationController cc) throws Exception {
        if (cancelled) return;
        panel.setProgress(10);
        cc.toPhase(Phase.RESOLVED);
        CompilationUnitTree unit = cc.getCompilationUnit();
        panel.setProgress(20);
        TreePath path = handle.resolve(cc);
        if (path == null) {
            throw new IOException("Could not resolve " + path);
        }
        Element el = cc.getTrees().getElement(path);
        if (el == null || el.getKind() != ElementKind.METHOD) {
           setProblem (NbBundle.getMessage(ChangeSignatureUI.class,
                   "MSG_NO_METHOD"));
           return;
        }
        ExecutableElement ee = (ExecutableElement) el;
        TypeElement containingClass = cc.getElementUtilities().enclosingTypeElement(ee);
        boolean mayHaveOverrides = !containingClass.getModifiers().contains(Modifier.FINAL);
        TypeMirror superType = containingClass.getSuperclass();
        List <? extends TypeMirror> types = containingClass.getInterfaces();
        boolean methodMayBeOverride = !"java.lang.Object".equals(superType.toString()) || !types.isEmpty();
        
        Collection<ElementHandle<ExecutableElement>> overrides;
        if (methodMayBeOverride) {
            overrides = Utils.getOverridingMethods(ee, cc);
        } else {
            overrides = Collections.<ElementHandle<ExecutableElement>>emptyList();
        }
        
        MethodTree tree = (MethodTree) path.getLeaf();
        String name = tree.getName().toString();
        String type = tree.getReturnType().toString();
        List <? extends VariableTree> params = tree.getParameters();
        Trees trees = cc.getTrees();
        List <Parameter> descs = new ArrayList <Parameter> (params.size());
        int ix = 0;
        panel.setProgress (70);
        for (VariableTree vt : params) {
            String nm = vt.getName().toString();
            TypeMirror typeMirror = trees.getTypeMirror(
                    TreePath.getPath(unit, vt));
            
            TypeMirrorHandle<TypeMirror> handle = 
                    TypeMirrorHandle.<TypeMirror>create(typeMirror);
            
            String typeName = typeMirror.toString();

            Parameter desc = new Parameter (nm, handle, ix, typeName);
            descs.add (desc);
            ix++;
        }
        panel.setProgress(100);
        panel.setMethodType (type);
        panel.setMethodName (name);
        panel.setMayHaveOverrides (mayHaveOverrides);
        //XXX how to deal with case where one method overrides methods in
        //two interfaces?
        panel.setOverrides (overrides);
        panel.setParameters (descs);
    }
}
