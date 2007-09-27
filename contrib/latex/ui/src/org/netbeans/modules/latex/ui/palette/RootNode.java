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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.latex.ui.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.ui.TexCloneableEditor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class RootNode extends AbstractNode {

    private static DataFolder folder;

    private static synchronized DataFolder getPaletteFolder() {
        if (folder == null) {
            FileObject file = Repository.getDefault().getDefaultFileSystem().findResource("LaTeXPalette");

            folder = DataFolder.findFolder(file);
            
            assert folder != null;
        }

        return folder;
    }

    /** Creates a new instance of RootNode */
    public RootNode() {
        super(new RootChildren(getPaletteFolder()), Lookups.singleton(getPaletteFolder()));
    }
    
    private static final class RootChildren extends Children.Keys {
        
        private DataFolder delegateTo;
        
        public RootChildren(DataFolder delegateTo) {
            this.delegateTo = delegateTo;
        }
        
        protected void addNotify() {
            List cath = new ArrayList(IconsStorage.getDefault().getCathegories());
            
            cath.remove("greek");
            cath.add(0, "greek");

            List keys = new ArrayList(cath);

            keys.addAll(Arrays.asList(delegateTo.getChildren()));

            setKeys(keys);
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof DataObject) {
                return new Node[] {((DataObject) key).getNodeDelegate()};
            } else {
                return new Node[] {new CategoryNode((String) key)};
            }
        }
        
    }
    
}
