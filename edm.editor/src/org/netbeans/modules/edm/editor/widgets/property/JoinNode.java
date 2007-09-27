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
package org.netbeans.modules.edm.editor.widgets.property;

import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 *
 * @author karthikeyan s
 */
public class JoinNode extends AbstractNode {
    
    private SQLJoinOperator joinOp;
    
    private MashupDataObject mObj;
    
    public JoinNode(SQLJoinOperator obj, MashupDataObject dObj) {
        super(Children.LEAF);
        mObj = dObj;
        joinOp = obj;
    }
    
    public JoinNode(SQLJoinOperator obj){
        super(Children.LEAF);
        joinOp = obj;
    }
    
    @Override
    public boolean canCopy() {
        return false;
    }
    
    @Override
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canCut() {
        return false;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
    
    /** Creates a property sheet. */
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        try {
            PropertySupport.Reflection joinConditionProp = new PropertySupport.Reflection(
                    this.joinOp.getJoinCondition(), String.class,
                    "getConditionText", "setConditionText");
            joinConditionProp.setName("Join Condition");
            joinConditionProp.setPropertyEditorClass(
                    PropertyEditorManager.getPropertyEditor("JOIN_CONDITION"));
            set.put(joinConditionProp);
            
            PropertySupport.Reflection joinTypeProp = new PropertySupport.Reflection(
                    this.joinOp, String.class,
                    "getJoinTypeString", "setJoinType");
            joinTypeProp.setName("Join Type");
            joinTypeProp.setPropertyEditorClass(
                    PropertyEditorManager.getPropertyEditor("JOIN_TYPE"));
            set.put(joinTypeProp);
            
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        sheet.put(set);
        return sheet;
    }
    
    public SQLJoinOperator getJoinOperator() {
        return this.joinOp;
    }
    
    public MashupDataObject getMashupDataObject() {
        return mObj;
    }
}