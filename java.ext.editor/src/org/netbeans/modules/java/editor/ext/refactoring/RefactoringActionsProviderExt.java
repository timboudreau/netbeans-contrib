/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring;

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.Collection;
import java.util.HashSet;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.java.editor.ext.refactoring.inline.InlineRefactoringUI;
import org.netbeans.modules.refactoring.java.RetoucheUtils;
import org.netbeans.modules.refactoring.java.ui.RefactoringActionsProvider;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author lahvac
 */
public class RefactoringActionsProviderExt extends RefactoringActionsProvider {

    private static RefactoringUI doInlineImpl(TreePathHandle selectedTPH, CompilationInfo info) {
        TreePath selected = selectedTPH.resolve(info);
        if (selected == null) {
            return null; //XXX: log
        }
        Element selectedElement = info.getTrees().getElement(selected);
        if (selectedElement==null) {
//            logger().log(Level.INFO, "doRename: " + selectedElement, new NullPointerException("selected")); // NOI18N
            return null;
        }

        boolean declaration;
        String  error = null;

        switch (selected.getLeaf().getKind()) {
            case VARIABLE:
                declaration = true;
                VariableTree vt = (VariableTree) selected.getLeaf();
                
                if (vt.getInitializer() == null) {
                    error = "No initializer";
                }
                break;
            default:
                declaration = false;
                break;
        }

        if (error == null) {
            return new InlineRefactoringUI(selectedTPH, declaration, -1);
        }

        return null;
    }

    public static void doInlineImpl(final Lookup lookup) {
        Runnable task;
        EditorCookie ec = lookup.lookup(EditorCookie.class);
        if (isFromEditor(ec)) {
            task = new TextComponentTask(ec) {
                @Override
                protected RefactoringUI createRefactoringUI(TreePathHandle selectedElement,int startOffset,int endOffset, final CompilationInfo info) {
                    return doInlineImpl(selectedElement, info);
                }
            };
        } else {
            task = new TreePathHandleTask(new HashSet<Node>(lookup.lookupAll(Node.class)), true) {

                RefactoringUI ui;

                @Override
                protected void treePathHandleResolved(TreePathHandle handle, CompilationInfo javac) {
                    ui = doInlineImpl(handle, javac);
                }

                @Override
                protected RefactoringUI createRefactoringUI(Collection<TreePathHandle> handles) {
                    return ui;
                }

            };
        }

        RetoucheUtils.invokeAfterScanFinished(task, "Inline");//getActionName(RefactoringActionsFactory.renameAction()));
    }

//    static String getActionName(Action action) {
//        String arg = (String) action.getValue(Action.NAME);
//        arg = arg.replace("&", ""); // NOI18N
//        return arg.replace("...", ""); // NOI18N
//    }


    public static boolean canInline(Lookup lookup) {
        Collection<? extends Node> nodes = new HashSet<Node>(lookup.lookupAll(Node.class));
        if(nodes.size() != 1)
            return false;
        Node node = nodes.iterator().next();
        TreePathHandle tph = node.getLookup().lookup(TreePathHandle.class);
        if (tph != null) {
            return RetoucheUtils.isRefactorable(tph.getFileObject());
        }
        DataObject dObj = node.getCookie(DataObject.class);
        if(null == dObj)
            return false;
        FileObject fileObj = dObj.getPrimaryFile();
        if(null == fileObj || !RetoucheUtils.isRefactorable(fileObj))
            return false;

        EditorCookie ec = lookup.lookup(EditorCookie.class);
        if (isFromEditor(ec)) {
            return true;
        }
        return false;
    }

    //XXX: copied from RefactoringActionsProvider:\
    static boolean isFromEditor(EditorCookie ec) {
        if (ec != null && ec.getOpenedPanes() != null) {
            TopComponent activetc = TopComponent.getRegistry().getActivated();
            if (activetc instanceof CloneableEditorSupport.Pane) {
                return true;
            }
        }
        return false;
    }

    static boolean nodeHandle(Lookup lookup) {
        Node n = lookup.lookup(Node.class);
        if (n!=null) {
            if (n.getLookup().lookup(TreePathHandle.class)!=null)
                return true;
        }
        return false;
    }
}
