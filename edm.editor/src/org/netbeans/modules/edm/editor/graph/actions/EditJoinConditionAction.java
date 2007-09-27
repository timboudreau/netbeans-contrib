/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */

package org.netbeans.modules.edm.editor.graph.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.ui.graph.IOperatorXmlInfoModel;
import org.netbeans.modules.sql.framework.ui.view.IGraphViewContainer;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderView;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;

/**
 *
 * @author karthikeyan s
 */
public class EditJoinConditionAction extends AbstractAction {
    
    private SQLJoinOperator joinOp;
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public EditJoinConditionAction(MashupDataObject dObj, SQLJoinOperator op) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        joinOp = op;
        this.manager = dObj.getGraphManager();
    }
    
    public EditJoinConditionAction(MashupDataObject dObj, SQLJoinOperator op, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        joinOp = op;
        this.manager = dObj.getGraphManager();
    }
    
    public void actionPerformed(ActionEvent e) {
        if (joinOp != null && mObj.getEditorView() != null) {
            
            List srcTables = joinOp.getAllSourceTables();
            if(mObj.getModel().getSQLDefinition().getRuntimeDbModel() != null &&
                    mObj.getModel().getSQLDefinition().getRuntimeDbModel().getRuntimeInput()!= null) {
                srcTables.add(mObj.getModel().getSQLDefinition().getRuntimeDbModel().getRuntimeInput());
            }
            
            ConditionBuilderView conditionView = new ConditionBuilderView(
                    (IGraphViewContainer) mObj.getEditorView().getGraphView().getGraphViewContainer(),
                    srcTables, joinOp.getJoinCondition(), IOperatorXmlInfoModel.CATEGORY_FILTER);
            DialogDescriptor dd = new DialogDescriptor(conditionView, "Edit Join Condition", 
                    true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
            
            if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
                SQLCondition cond = (SQLCondition) conditionView.getPropertyValue();
                if (cond != null) {
                    SQLCondition oldCondition = joinOp.getJoinCondition();
                    if (joinOp != null && !cond.equals(oldCondition)) {
                        joinOp.setJoinCondition(cond);
                        joinOp.setJoinConditionType(SQLJoinOperator.USER_DEFINED_CONDITION);
                        mObj.getMashupDataEditorSupport().synchDocument();
                        manager.setLog("Join Condition sucessfully modified");                        
                    }
                }
            }
        }
    }
}