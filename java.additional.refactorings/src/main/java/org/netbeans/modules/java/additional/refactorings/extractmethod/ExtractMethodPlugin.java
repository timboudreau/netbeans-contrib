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

import org.netbeans.modules.java.additional.refactorings.*;
import java.io.IOException;
import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.visitors.ExceptionsVisitor;
import org.netbeans.modules.java.additional.refactorings.visitors.ParamDesc;
import org.netbeans.modules.java.additional.refactorings.visitors.ReturnTypeVisitor;
import org.netbeans.modules.java.additional.refactorings.visitors.UsedLocalVariableVisitor;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public final class ExtractMethodPlugin extends Refactoring {
    private final FileObject file;
    private final int start;
    private final int end;
    private final TreePathHandle handle;
    private final ExtractMethodRefactoring refactoring;
    public ExtractMethodPlugin(ExtractMethodRefactoring refactoring, FileObject file, int start, int end, TreePathHandle handle) {
        this.refactoring = refactoring;
        this.file = file;
        this.start = start;
        this.end = end;
        this.handle = handle;
    }

    protected Problem preCheck(CompilationController javac) throws IOException {
        if (start == end || start < 0 || end < 0) {
            return new Problem (true, getS("MSG_NOTHING_TO_MOVE")); //NOI18N
        }
        return null;
    }

    protected Problem checkParameters(CompilationController wc) throws IOException {
        wc.toPhase(Phase.RESOLVED);
        SourcePositions positions = wc.getTrees().getSourcePositions();
        CompilationUnitTree compUnit = wc.getCompilationUnit();
        Element el = handle.resolveElement(wc);
        TreePath path = wc.getTrees().getPath(el);
        Tree     tree = path.getLeaf();
        
        BlockTree blockTree = Utils.findBlockTree(tree);
        if (blockTree == null) {
            return new Problem (true, getS("MSG_NO_STATEMENTS_TO_MOVE")); //NOI18N
        }
        Set <ParamDesc> usedElements = new HashSet <ParamDesc> ();
        TreePath blockPath = TreePath.getPath(compUnit, blockTree);
        TreePath startPath = wc.getTreeUtilities().pathFor(start);
        TreePath endPath = wc.getTreeUtilities().pathFor(end);
        if (!startPath.getParentPath().getLeaf().equals(endPath.getParentPath().getLeaf())) {
            return new Problem (true, getS("MSG_UNBALANCED_BLOCKS"));            
        }
        
        List <? extends StatementTree> statements = blockTree.getStatements();        
        
        List <StatementTree> toMove = new ArrayList <StatementTree>(
                statements.size());
        
        Set <TypeMirror> returnTypes = new HashSet <TypeMirror> ();
        
        UsedLocalVariableVisitor usedLocalsVisitor = 
                new UsedLocalVariableVisitor (wc, start, end);
        
        ReturnTypeVisitor returnTypeVisitor = new ReturnTypeVisitor (wc);
        
        for (Iterator <? extends StatementTree> it = statements.iterator(); it.hasNext();) {
            StatementTree st = it.next();
            long statementStart = positions.getStartPosition(compUnit, st);
            long statementEnd = positions.getEndPosition(compUnit, st);
            boolean include = statementStart >= this.start && statementEnd <= this.end;
            if (include) {
                TreePath pathToStatement = new TreePath(blockPath, st);
                usedLocalsVisitor.scan(pathToStatement, usedElements);
                toMove.add (st);
                returnTypeVisitor.scan (pathToStatement, returnTypes);
            }
        }
        if (toMove.isEmpty()) {
            return new Problem (true, getS("MSG_NOTHING_TO_MOVE")); //NOI18N
        } else if (usedLocalsVisitor.getLocallyAssigned().size() > 1) {
            return new Problem (true, getS("MSG_LOCALS_ASSIGNED", usedLocalsVisitor.getLocallyAssigned())); //NOI18N
        } else if (usedLocalsVisitor.getLocallyAssigned().size() == 1 && returnTypes.size() > 0) {
            return new Problem (true, getS("MSG_NO_ROOM_TO_RETURN")); //NOI18N
        }
        
        locallyAssigned = usedLocalsVisitor.getLocallyAssigned();
        statementHandles = Utils.toHandles(blockPath, 
                toMove, wc);
        
        names2types = usedLocalsVisitor.getNames2Types();
        
        TypeMirror returnType = returnTypeVisitor.getReturnType(returnTypes);
        returnTypeHandle = returnType == null ? null : 
            TypeMirrorHandle.<TypeMirror>create(returnType);

        if (locallyAssigned.size() == 1) {
            returnTypeHandle = names2types.get(
                    locallyAssigned.iterator().next());
        }
        
        return null;
    }
    
    private Set <String> locallyAssigned;
    private Map <String, TypeMirrorHandle<TypeMirror>> names2types;
    private List <TreePathHandle> statementHandles;
    private TypeMirrorHandle <TypeMirror> returnTypeHandle;

    protected Problem fastCheckParameters(CompilationController wc) throws IOException {
        return null;
    }

    protected Problem prepare(WorkingCopy wc, RefactoringElementsBag bag) throws IOException {
        assert locallyAssigned != null;
        assert names2types != null;
        assert statementHandles != null;
        wc.toPhase(Phase.RESOLVED);
        SourcePositions positions = wc.getTrees().getSourcePositions();
        CompilationUnitTree compUnit = wc.getCompilationUnit();
        Element el = handle.resolveElement(wc);
        TreePath path = wc.getTrees().getPath(el);
        Tree     tree = path.getLeaf();
        
        BlockTree blockTree = Utils.findBlockTree(tree);
        
        TreePath blockPath = TreePath.getPath(compUnit, blockTree);
        List <? extends StatementTree> statements = blockTree.getStatements();        

        ExceptionsVisitor exceptionsVisitor = new ExceptionsVisitor(wc);
        
        Set <ParamDesc> usedElements = new HashSet <ParamDesc> ();
        Set <TypeMirrorHandle> exceptions = new HashSet <TypeMirrorHandle> ();
        
        boolean lastLineReturns = false;
        for (Iterator <? extends StatementTree> it = statements.iterator(); it.hasNext();) {
            StatementTree st = it.next();
            long statementStart = positions.getStartPosition(compUnit, st);
            long statementEnd = positions.getEndPosition(compUnit, st);
            boolean include = statementStart >= this.start && statementEnd <= this.end;
            if (include) {
                TreePath pathToStatement = new TreePath(blockPath, st);
                st.accept (exceptionsVisitor, exceptions);
                if (!it.hasNext()) {
                    ReturnTypeVisitor lastLineDetector = new ReturnTypeVisitor(wc);
                    Set <TypeMirror> used = new HashSet <TypeMirror> ();
                    lastLineDetector.scan(pathToStatement, used);
                    lastLineReturns = lastLineDetector.getReturnType(used) != null;
                }
                st.accept(exceptionsVisitor, exceptions);
            }
        }
        
        TreePathHandle handle = TreePathHandle.create(path, wc);    
        bag.add (refactoring, 
                new ExtractMethodElementImpl(refactoring.methodName, 
                getFileObject(),
                handle, usedElements, 
                exceptions, statementHandles, returnTypeHandle, 
                locallyAssigned.isEmpty() ? null :
                    locallyAssigned.iterator().next(),
                lastLineReturns, refactoring.modifiers));
        
        return null;
    }
    
    protected FileObject getFileObject() {
        return file;
    }
    
    static String getS (String key) {
        return NbBundle.getMessage (ExtractMethodPlugin.class, key);
    }
    
    static String getS (String key, Object... params) {
        return NbBundle.getMessage (ExtractMethodPlugin.class, key, params);
    }
}
