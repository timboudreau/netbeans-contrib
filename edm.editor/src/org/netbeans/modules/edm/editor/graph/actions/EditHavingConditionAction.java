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
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.sql.framework.model.SQLCondition;
import org.netbeans.modules.sql.framework.ui.view.IGraphViewContainer;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderView;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLObject;
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;
import org.netbeans.modules.sql.framework.ui.view.conditionbuilder.ConditionBuilderUtil;

/**
 * This is an action class for invoking having condition editor.
 * @author karthikeyan s
 */
public class EditHavingConditionAction extends AbstractAction {
    
    private SQLGroupByImpl grpby;
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public EditHavingConditionAction(MashupDataObject dObj, SQLGroupByImpl op) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        grpby = op;
        this.manager = dObj.getGraphManager();
    }
    
    /** Creates a new instance of EditJoinAction */
    public EditHavingConditionAction(MashupDataObject dObj, SQLGroupByImpl op, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.JOINCONDITION)));
        mObj = dObj;
        grpby = op;
        this.manager = dObj.getGraphManager();
    }
    
    /**
     * Implements action performed.     
     */
    public void actionPerformed(ActionEvent e) {
        if (grpby != null && mObj.getEditorView() != null) {
            ConditionBuilderView conditionView = ConditionBuilderUtil.getHavingConditionBuilderView(getParentObject(),
                    (IGraphViewContainer)mObj.getEditorView().getGraphView().getGraphViewContainer());
            DialogDescriptor dd = new DialogDescriptor(conditionView, "Edit Join Condition", true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
            
            if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
                SQLCondition cond = (SQLCondition) conditionView.getPropertyValue();
                if (cond != null) {
                    SQLCondition oldCondition = grpby.getHavingCondition();
                    if (grpby != null && !cond.equals(oldCondition)) {
                        grpby.setHavingCondition(cond);                        
                        mObj.getMashupDataEditorSupport().synchDocument();
                        manager.setLog("Having clause sucessfully modified");
                    }
                }
            }
        }
    }
    
    /*
     *  method to object parent object for the given sql group by impl object.
     */
    private SQLObject getParentObject() {
        SQLObject obj = (SQLObject) grpby.getParentObject();
        return mObj.getModel().getSQLDefinition().getObject(obj.getId(), obj.getObjectType());        
    }    
}