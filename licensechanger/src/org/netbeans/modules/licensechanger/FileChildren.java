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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 */
public class FileChildren extends ChildFactory.Detachable<FileObject> {

    private final Set<FileObject> folders;
    private final Collection<? extends FileHandler> allHandlers;
    private final Map<Object, Object> settings;
    private final Set<FileItem> items = Collections.synchronizedSet(new HashSet<FileItem>());

    FileChildren(Set<FileObject> folders, Map<Object, Object> settings) {
        this.settings = settings;
        this.folders = new HashSet<FileObject>(folders);
        allHandlers = Lookup.getDefault().lookupAll(FileHandler.class);
        settings.put(PreviewPanel.KEY_ITEMS, items);
    }

    @Override
    protected boolean createKeys(List<FileObject> toPopulate) {
        toPopulate.addAll(folders);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(FileObject key) {
        List<Node> result = new LinkedList<Node>();
        for (FileObject fo : key.getChildren()) {
            if (fo.isData()) {
                for (FileHandler h : allHandlers) {
                    if (h.match(fo)) {
                        try {
                            DataObject dob = DataObject.find(fo);
                            FileItem item = new FileItem(h, fo);
                            items.add(item);
                            FN fn = new FN (dob.getNodeDelegate(), item);
                            boolean shouldChange =  !h.shouldSkipFile(fo);
                            result.add(fn);
                            fn.setValue (CheckboxListView.SELECTED, shouldChange);
                            break;
                        } catch (DataObjectNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }
        return result.toArray(new Node[result.size()]);
    }

    static final class FileItem {

        FileHandler handler;
        FileObject file;

        public FileItem(FileHandler handler, FileObject file) {
            this.handler = handler;
            this.file = file;
        }

        public FileObject getFile() {
            return file;
        }

        public FileHandler getHandler() {
            return handler;
        }
    }

    private static final class FN extends FilterNode {

        FN(Node orig, FileItem item) {
            super(orig, Children.LEAF, new ProxyLookup(
                    Lookups.singleton(item),
                    orig.getLookup()));
            disableDelegation(DELEGATE_GET_ACTIONS);
            disableDelegation(DELEGATE_GET_CONTEXT_ACTIONS);
            disableDelegation(DELEGATE_SET_DISPLAY_NAME);
            disableDelegation(DELEGATE_GET_DISPLAY_NAME);
            disableDelegation(DELEGATE_GET_VALUE);
            disableDelegation(DELEGATE_SET_VALUE);
            disableDelegation(DELEGATE_SET_NAME);
            disableDelegation(DELEGATE_GET_NAME);
            setValue(CheckboxListView.SELECTED, Boolean.TRUE);
            setName(item.file.getPath());
            setDisplayName(item.file.getPath());
            setShortDescription(item.file.getPath());
        }

        @Override
        public Action[] getActions(boolean ignored) {
            return new Action[0];
        }
    }
}
