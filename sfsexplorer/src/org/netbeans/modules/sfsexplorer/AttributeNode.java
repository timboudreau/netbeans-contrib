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
package org.netbeans.modules.sfsexplorer;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Node representing an attribute.
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com), David Strupl
 */
class AttributeNode extends AbstractNode {
    private Node of;
    private String attributeName;
    private Object attributeValue;
    private String platform;
    private Action[] actions;
    
    /**
     * 
     * @param of 
     * @param attributeName 
     * @param attributeValue 
     * @param platform 
     */
    AttributeNode(Node of, String attributeName, Object attributeValue, String platform) {
        super(Children.LEAF);
        this.of = of;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.platform = platform;
        setName(attributeName + "=" + attributeValue);
        setIconBaseWithExtension("org/netbeans/modules/sfsexplorer/attributeNode.gif");
        setShortDescription(NbBundle.getMessage(AttributeNode.class, "This_node_represents_an_attribute_attached_to_the_parent_node."));
    }
    
    /**
     * 
     * @param context 
     * @return 
     */
    public Action[] getActions(boolean context) {
        if (!context) {
            if (actions == null) {
                return getActions(of);
            }
            return actions;
        }
        return SFSBrowserTopComponent.EMPTY_ACTIONS;
    }
    
    /**
     * 
     * @param node 
     * @return 
     */
    private Action[] getActions(Node node) {
        List<Action> actions = new LinkedList<Action>();
        if ("instanceClass".equals(attributeName)) {
//                actions.add(new GotoJavaTypeAction(String.valueOf(attributeValue)));
        } else {
            if ("originalFile".equals(attributeName)) {
                String originalFile = String.valueOf(attributeValue);
                if (originalFile != null && originalFile.endsWith(".instance")) {
                    final String originalFileSansExt = originalFile.substring(0, originalFile.lastIndexOf(".instance"));
                    actions.add(new AbstractAction(NbBundle.getMessage(AttributeNode.class, "Go_to_original_file_") + originalFileSansExt) {
                        public void actionPerformed(ActionEvent e) {
                            SFSBrowserTopComponent.select(originalFileSansExt);
                        }
                    });
                }
            }
        }
        MultiFileSystem multiFileSystem = (MultiFileSystem) Repository.getDefault().getDefaultFileSystem();
        FileObject root = multiFileSystem.getRoot();
        SFSBrowserTopComponent.collectActions(node, actions, platform, root);
        return actions.toArray(SFSBrowserTopComponent.EMPTY_ACTIONS);
    }
}
