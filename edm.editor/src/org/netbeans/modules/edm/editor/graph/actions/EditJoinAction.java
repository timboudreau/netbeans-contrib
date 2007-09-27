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

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.ui.view.join.JoinMainDialog;
import org.netbeans.modules.sql.framework.ui.view.join.JoinUtility;

/**
 *
 * @author karthikeyan s
 */
public class EditJoinAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    /** Creates a new instance of EditJoinAction */
    public EditJoinAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITJOIN)));
        mObj = dObj;
    }
    
    public EditJoinAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITJOIN)));
        mObj = dObj;
    }    
    /** 
     * implements edit join action. 
     */    
    public void actionPerformed(ActionEvent e) {
        SQLJoinView[] joinViews = (SQLJoinView[]) mObj.getModel().getSQLDefinition().getObjectsOfType(
                SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
        SQLJoinView jView = null;
        if(joinViews != null && joinViews.length != 0) {
            jView = joinViews[0];
        }
        JoinMainDialog.showJoinDialog(
                mObj.getModel().getSQLDefinition().getJoinSources(), jView,
                null);
        if (JoinMainDialog.getClosingButtonState() == JoinMainDialog.OK_BUTTON) {
            SQLJoinView joinView = JoinMainDialog.getSQLJoinView();
            try {
                if (joinView != null) {
                    mObj.getModel().getSQLDefinition().removeObjects(
                            mObj.getModel().getSQLDefinition().getObjectsOfType(
                            SQLConstants.JOIN_VIEW));
                    JoinUtility.handleNewJoinCreation(joinView,
                            JoinMainDialog.getTableColumnNodes(),
                            mObj.getEditorView().getCollaborationView().getGraphView());
                    mObj.getMashupDataEditorSupport().synchDocument();
                    mObj.getGraphManager().generateGraph(mObj.getModel().getSQLDefinition());
                    //mObj.getGraphManager().getScene().layoutScene();
                    mObj.getGraphManager().setLog("Join view successfully edited.");
                }
            } catch (Exception ex) {
                 mObj.getGraphManager().setLog("Error adding Join view.");
            }
        }
    }
}