/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.devtools.astbrowser;

import com.oracle.nashorn.ir.AccessNode;
import com.oracle.nashorn.ir.BinaryNode;
import com.oracle.nashorn.ir.Block;
import com.oracle.nashorn.ir.BreakNode;
import com.oracle.nashorn.ir.CallNode;
import com.oracle.nashorn.ir.CaseNode;
import com.oracle.nashorn.ir.CatchNode;
import com.oracle.nashorn.ir.ContinueNode;
//import com.oracle.nashorn.ir.ErrorNode;
import com.oracle.nashorn.ir.ExecuteNode;
import com.oracle.nashorn.ir.ForNode;
import com.oracle.nashorn.ir.FunctionNode;
import com.oracle.nashorn.ir.IdentNode;
import com.oracle.nashorn.ir.IfNode;
import com.oracle.nashorn.ir.IndexNode;
import com.oracle.nashorn.ir.LabelNode;
import com.oracle.nashorn.ir.LineNumberNode;
import com.oracle.nashorn.ir.LiteralNode;
import com.oracle.nashorn.ir.Node;
import com.oracle.nashorn.ir.ObjectNode;
import com.oracle.nashorn.ir.PrintVisitor;
import com.oracle.nashorn.ir.PropertyNode;
import com.oracle.nashorn.ir.ReferenceNode;
import com.oracle.nashorn.ir.ReturnNode;
import com.oracle.nashorn.ir.RuntimeNode;
import com.oracle.nashorn.ir.SwitchNode;
import com.oracle.nashorn.ir.TernaryNode;
import com.oracle.nashorn.ir.ThrowNode;
import com.oracle.nashorn.ir.TryNode;
import com.oracle.nashorn.ir.UnaryNode;
import com.oracle.nashorn.ir.VarNode;
import com.oracle.nashorn.ir.WhileNode;
import com.oracle.nashorn.ir.WithNode;
import com.oracle.nashorn.parser.Token;
import java.awt.Component;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

//import org.netbeans.modules.php.editor.parser.astnodes.*;
import org.openide.util.Enumerations;
import com.oracle.nashorn.ir.NodeVisitor;


/**
 *
 * @author Petr Pisl
 */
public class TreeCreator {

    private TreeASTNodeAdapter parentNode;


    static protected class TreeASTNodeAdapter implements TreeNode {

        TreeASTNodeAdapter parent;
        List<TreeASTNodeAdapter> children;
        String description;
        String tooltip;
        final int start;
        final int end;

        TreeASTNodeAdapter(TreeASTNodeAdapter parent, String description) {
            this(parent, description, null, -1, -1);
        }

        TreeASTNodeAdapter(TreeASTNodeAdapter parent, String description, int start, int end) {
            this(parent, description, null, start, end);
        }
        
        TreeASTNodeAdapter(TreeASTNodeAdapter parent, String description, String tooltip, int start, int end) {
            this.parent = parent;
            this.description = description;
            children = new ArrayList<TreeASTNodeAdapter>();
            this.start = start;
            this.end = end;
            this.tooltip = tooltip;
        }

        public void addChild(TreeASTNodeAdapter child) {
            children.add(child);
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return children.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return children.size();
        }

        @Override
        public TreeNode getParent() {
            return parent;
        }

        @Override
        public int getIndex(TreeNode node) {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == node) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public boolean getAllowsChildren() {
            return children.size() > 0;
        }

        @Override
        public boolean isLeaf() {
            return children.isEmpty();
        }

        @Override
        public Enumeration children() {
            return Enumerations.array(children);
        }

        
        
        @Override
        public String toString() {
            StringBuilder text = new StringBuilder();
            text.append("<html>");

            if (start > -1) {
                text.append("[").append(start).append(", ").append(end).append("] ");
            }
            text.append(description);
            text.append("</html>");
            return text.toString();
        }

        public int getStartOffset() {
            return start;
        }

        public int getEndOffset() {
            return end;
        }
    }

    static protected class TreeNodeCellRenderer extends DefaultTreeCellRenderer {

        public TreeNodeCellRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof TreeCreator.TreeASTNodeAdapter) {
                setToolTipText(((TreeCreator.TreeASTNodeAdapter)value).tooltip);
            }
            return this;
        }
    }

    public TreeNode createTree(com.oracle.nashorn.ir.Node node, boolean detailed) {
        //System.out.println((new PrintVisitor()).toString(node));
        if (!detailed) {
            parentNode = new TreeASTNodeAdapter(null, "Nashorn Parser output");
            node.accept(new SimpleTreeNodeVisitor(parentNode));
        } else {
            parentNode = new TreeASTNodeAdapter(null, "Detail Nashorn AST");
            node.accept(new DetailTreeNodeVisitor(parentNode));
        }
        return parentNode;
    }


    
    
    
            

}
