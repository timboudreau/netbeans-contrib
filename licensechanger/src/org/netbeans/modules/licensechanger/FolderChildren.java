/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.licensechanger;

import java.awt.EventQueue;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
abstract class FolderChildren extends ChildFactory.Detachable<FileObject> implements Runnable {
    private FileObject[] roots;
    volatile int keyCount = 0;
    private int nodeCount = 0;
    private volatile boolean cancelled = false;

    FolderChildren (FileObject[] roots) {
        this.roots = roots;
    }

    @Override
    protected Node createWaitNode() {
        AbstractNode result = new AbstractNode(Children.LEAF);
        result.setDisplayName(NbBundle.getMessage(FolderChildren.class, "MSG_WAIT_FOLDERS"));
        return result;
    }

    @Override
    public void addNotify() {
        cancelled = false;
        super.addNotify();
        keyCount = 0;
        nodeCount = 0;
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        cancelled = true;
    }

    private boolean shouldSkipFolder (FileObject folder) {
        // XXX use VisibilityQuery instead
        String path = folder.getPath();
        return path.contains(".svn") || path.contains(".cvs") || path.contains(".hg") ||
                path.endsWith(".svn") || path.endsWith(".cvs") || path.endsWith(".hg");
    }

    @Override
    protected boolean createKeys(List<FileObject> toPopulate) {
        for (FileObject f : roots) {
            toPopulate.add(f);
            keyCount++;
            for (FileObject fo : NbCollections.iterable(f.getChildren(true))) {
                if (fo.isFolder()) {
                    if (shouldSkipFolder(fo)) {
                        continue;
                    }
                    toPopulate.add(fo);
                    keyCount++;
                }
                if (cancelled) {
                    return true;
                }
            }
            if (cancelled) {
                return true;
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(FileObject f) {
        try {
            Node n = DataObject.find(f).getNodeDelegate();
            nodeCount++;
            if (nodeCount == keyCount) {
                EventQueue.invokeLater(this);
            }
            return new FN(n, f);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public abstract void onAllNodesCreated();

    public void run() {
        onAllNodesCreated();
    }

    private static final class FN extends FilterNode {
        FN(Node orig, FileObject f) {
            super (orig, Children.LEAF, new ProxyLookup(
                    Lookups.singleton(f),
                    orig.getLookup()));
            disableDelegation(DELEGATE_GET_ACTIONS);
            disableDelegation(DELEGATE_GET_CONTEXT_ACTIONS);
            disableDelegation(DELEGATE_SET_DISPLAY_NAME);
            disableDelegation(DELEGATE_GET_DISPLAY_NAME);
            disableDelegation(DELEGATE_GET_VALUE);
            disableDelegation(DELEGATE_SET_VALUE);
            disableDelegation(DELEGATE_SET_NAME);
            disableDelegation(DELEGATE_GET_NAME);
            String p = f.getPath();
            // XXX this is a pretty arbitrary list... maybe check SharabilityQuery?
            if (!p.endsWith("nbproject") &&
                !p.endsWith("nbproject/private")) { //NOI18N
                setValue (CheckboxListView.SELECTED, Boolean.TRUE);
            }
            setName(p);
            setDisplayName(FileUtil.getFileDisplayName(f));
        }

        @Override
        public Action[] getActions(boolean ignored) {
            return new Action[0];
        }
    }
}
