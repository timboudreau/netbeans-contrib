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

package org.netbeans.modules.jemmysupport.generator;

import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/** Filter nodes to display only folders (that ones which has DataFolder cookie)
 *
 * @author  Jiri.Skrivanek@sun.com
 */
final class DataFolderFilterNode extends FilterNode {

    /** Creates new DataFolderFilterNode. */
    public DataFolderFilterNode(Node node) {
        super(node, new DataFolderFilterChildren(node));
    }

    private static class DataFolderFilterChildren extends FilterNode.Children {
        
        /** Creates new DataFolderFilterChildren. */
        public DataFolderFilterChildren(Node node) {
            super(node);
        }
        
        protected Node[] createNodes(Node node) {
            // without filtering
            // return new Node[] { copyNode(n) };
  
            DataObject dataObj = (DataObject)node.getCookie(DataObject.class);
            if(dataObj != null) {
                // if data object exists
                Object dataFolder = node.getCookie(org.openide.loaders.DataFolder.class);
                if(dataFolder == null) {
                    // if it is not a data folder, don't show it
                    return new Node[] {};
                }
            }
            // otherwise continue recursively
            return new Node[] { new DataFolderFilterNode(node) };
        }
    }
}
