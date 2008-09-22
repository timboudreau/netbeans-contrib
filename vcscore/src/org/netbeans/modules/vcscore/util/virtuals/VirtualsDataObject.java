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

package org.netbeans.modules.vcscore.util.virtuals;

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author  Martin Entlicher
 */
public class VirtualsDataObject extends MultiDataObject {


    /** Creates new VersioningDataObject */
    public VirtualsDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
    }

    /** Get the name of the data object.
    * @return the name with the extension
    */
    public String getName () {
        return getPrimaryFile ().getNameExt();
    }

    /** Provide node that should represent this data object.
    * @return the node representation for this data object
    */
    protected Node createNodeDelegate () {
        DataNode node = new VirtualsDataNode (this, Children.LEAF);
        return node;
    }

    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    public class VirtualsDataNode extends DataNode {
        
        public VirtualsDataNode(MultiDataObject obj, Children childs) {
            super(obj, childs);
            //            setName(obj.getPrimaryFile().getNameExt());
            //            setIconBase(
            updateDisplayName();
            setShortDescription(NbBundle.getMessage(VirtualsDataNode.class, "VirtualsDataNode.Description"));
        }
        
        /** Changes the name of the node and may also rename the data object.
         * If the object is renamed and file extensions are to be shown,
         * the display name is also updated accordingly.
         *
         * @param name new name for the object
         * @param rename rename the data object?
         * @exception IllegalArgumentException if the rename failed
         */
        public void setName(String name, boolean rename) {
            super.setName(name, rename);
            if (rename) updateDisplayName();
        }
        
        private void updateDisplayName() {
            FileObject prim = getDataObject().getPrimaryFile();
            String newDisplayName = null;
            
            
            String ext = prim.getExt();
            newDisplayName = ext == null || ext.equals("") ? // NOI18N
                             prim.getName() : prim.getName() + '.' + ext; // NOI18N
            if (displayFormat != null)
                setDisplayName(displayFormat.format(new Object[] { newDisplayName }));
            else
                setDisplayName(newDisplayName);
        }
        
        /**
         * Returns <code>null</code> preferred action.
         */
        public javax.swing.Action getPreferredAction() {
            return null;
        }
        
        
    }
    
}
