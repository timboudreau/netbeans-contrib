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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.edm.editor.widgets.property.editor;

import java.beans.PropertyEditorSupport;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

import org.netbeans.modules.edm.editor.widgets.property.JoinNode;
import org.netbeans.modules.sql.framework.model.SQLConstants;

/**
 *
 * @author Nithya
 */
public class JoinTypeCustomEditor extends PropertyEditorSupport implements
        ExPropertyEditor {   
    
    private JoinNode node;
    
    private PropertyEnv env;
    
    public JoinTypeCustomEditor() {
        initialize();
    }

    @Override 
    public String[] getTags() {
        String[] tags = {"INNER JOIN","LEFT OUTER JOIN",
        "RIGHT OUTER JOIN","FULL OUTER JOIN"};
        return tags;
    }
    
    @Override
    public Object getValue(){        
        String type = "";
        if(node == null) {
            initialize();
        }
        int joinType = node.getJoinOperator().getJoinType();
        switch(joinType) {
        case SQLConstants.INNER_JOIN:
            type = "INNER JOIN";
            break;
        case SQLConstants.LEFT_OUTER_JOIN:
            type = "LEFT OUTER JOIN";
            break;
        case SQLConstants.RIGHT_OUTER_JOIN:
            type = "RIGHT OUTER JOIN";
            break;
        case SQLConstants.FULL_OUTER_JOIN:            
            type = "FULL OUTER JOIN";        
        }
        return type;
    }     
    
    @Override
    public String getAsText() {
        return (String)getValue();
    }
    
    @Override
    public void setValue(Object object) {
        String type = (String)object;
        if(type.equals("INNER JOIN")) {
            node.getJoinOperator().setJoinType(SQLConstants.INNER_JOIN);
        } else if(type.equals("LEFT OUTER JOIN")) {
            node.getJoinOperator().setJoinType(SQLConstants.LEFT_OUTER_JOIN);
        } else if(type.equals("RIGHT OUTER JOIN")) {
            node.getJoinOperator().setJoinType(SQLConstants.RIGHT_OUTER_JOIN);
        } else if(type.equals("FULL OUTER JOIN")) {
            node.getJoinOperator().setJoinType(SQLConstants.FULL_OUTER_JOIN);
        }
        node.getMashupDataObject().getMashupDataEditorSupport().synchDocument();
    }
    
    @Override
    public void setAsText(String text) {
        if(node == null) {
            initialize();
        }
        setValue(text);
    }
    
    private void initialize() {
        Node[] nodes = WindowManager.getDefault().getRegistry().getActivatedNodes();
        for(Node node : nodes) {
            if(node instanceof JoinNode) {
                this.node = (JoinNode) node;
                break;
            }
        }
    }

    public void attachEnv(PropertyEnv env) {
        this.env = env;
    }
}