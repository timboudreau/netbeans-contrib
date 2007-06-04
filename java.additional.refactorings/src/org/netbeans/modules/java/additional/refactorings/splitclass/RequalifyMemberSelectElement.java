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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.IOException;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public class RequalifyMemberSelectElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy> {
    private final TreePathHandle toRenameIn;
    private final TreePathHandle memberSelectToRequalify;
    private final String memberName;
    private final FileObject file;
    private final Lookup context;
    private final String where;
    private final String requalifyString;
    
    public RequalifyMemberSelectElement(TreePathHandle toRequalifyIn, TreePathHandle methodSelectToRequalify, 
            String memberName, String where, String requalifyString, Lookup context, FileObject file) {
        this.toRenameIn = toRequalifyIn;
        this.requalifyString = requalifyString;
        this.memberSelectToRequalify = methodSelectToRequalify;
        this.where = where;
        this.memberName = memberName;
        this.context = context;
        this.file = file;
    }

    public String getText() {
        return "Requalify reference to " + memberName + " in " + where; //XXX I18N
    }

    public String getDisplayText() {
        return getText();
    }

    public void performChange() {
        JavaSource js = JavaSource.forFileObject (file);
        try {
            js.runModificationTask(this).commit();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    public Lookup getLookup() {
        return context;
    }

    public FileObject getParentFile() {
        return file;
    }

    public PositionBounds getPosition() {
        return null;
    }

    volatile boolean cancelled;
    public void cancel() {
        cancelled = true;
    }

    public void run(WorkingCopy copy) throws Exception {
        TreePath pathToMethod = toRenameIn.resolve(copy);
        TreePath pathToMemberSelect = memberSelectToRequalify.resolve(copy);
        Tree tree = pathToMemberSelect.getLeaf();
        TreeMaker maker = copy.getTreeMaker();
        System.err.println("Requalify " + tree);
        
        if (tree.getKind() == Kind.MEMBER_SELECT) {
            MemberSelectTree toRequalify = (MemberSelectTree) pathToMemberSelect.getLeaf();
            MethodTree method = (MethodTree) pathToMethod.getLeaf ();
            maker.MemberSelect(toRequalify.getExpression(), requalifyString);
            //XXX what to do here?
            System.err.println("Requalify member select not implemented yet - " + toRequalify + " to " + requalifyString);
        } else {
            IdentifierTree toRequalify = (IdentifierTree) pathToMemberSelect.getLeaf();
            IdentifierTree nue = maker.Identifier(requalifyString + '.' + toRequalify.toString());
            copy.rewrite (toRequalify, nue);
        }
    }
}
