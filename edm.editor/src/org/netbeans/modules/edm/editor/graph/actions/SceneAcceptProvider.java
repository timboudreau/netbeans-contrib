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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.palette.Operator;
import org.netbeans.modules.edm.editor.widgets.EDMGraphScene;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.ui.view.join.JoinMainDialog;
import org.netbeans.modules.sql.framework.ui.view.join.JoinUtility;

/**
 * This class implements the accept provider.
 * This can accept the palette items and act accordingly.
 *
 * @author karthikeyan s
 */
public class SceneAcceptProvider implements AcceptProvider {
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    public SceneAcceptProvider(MashupDataObject dObj, MashupGraphManager manager) {
        this.mObj = dObj;
        this.manager = manager;
    }
    
    public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
        boolean accept = true;
        try {
            Object node = transferable.getTransferData(
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));
            Operator op = (Operator) node;
            String type = op.getName();
            if(type.equals("Join")) {
                if(mObj.getModel().getSQLDefinition().getJoinSources().size() == 0) {
                    accept = false;
                }
            }
        } catch (Exception ex) {
            accept = false;
        }
        if(!accept) {
            return ConnectorState.REJECT_AND_STOP;
        }
        return ConnectorState.ACCEPT;
    }
    
    public void accept(Widget widget, Point point, Transferable transferable) {
        try {
            Object nd = transferable.getTransferData(
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));
            Operator node = (Operator) nd;
            String type = node.getName();
            if(type.equals("Join")) {
                // create join widget on the canvas & create join operator and add to the model.
                if(mObj.getModel().getSQLDefinition().getJoinSources().size() != 0) {
                    SQLJoinView[] joinViews = (SQLJoinView[])mObj.getModel().getSQLDefinition().getObjectsOfType(
                            SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
                    if(joinViews == null || joinViews.length == 0) {
                        JoinMainDialog.showJoinDialog(
                                mObj.getModel().getSQLDefinition().getJoinSources(), null,
                                null, false);
                    } else {
                        JoinMainDialog.showJoinDialog(
                                mObj.getModel().getSQLDefinition().getJoinSources(), joinViews[0],
                                null);
                    }
                    if (JoinMainDialog.getClosingButtonState() == JoinMainDialog.OK_BUTTON) {
                        SQLJoinView joinView = JoinMainDialog.getSQLJoinView();
                        try {
                            if (joinView != null) {
                                JoinUtility.handleNewJoinCreation(joinView,
                                        JoinMainDialog.getTableColumnNodes(),
                                        mObj.getEditorView().getCollaborationView().getGraphView());
                                mObj.getMashupDataEditorSupport().synchDocument();
                                manager.refreshGraph();
                                manager.setLog("Join(s) sucessfully added.");
                            }
                        } catch (Exception ex) {
                            manager.setLog("Error adding join view.");
                        }
                    }
                }
            } else if(type.equals("Group By")) {
                // Add group by operator.
                if(mObj.getGraphManager().addGroupby(((EDMGraphScene)widget).
                        convertLocalToScene(point))) {
                    mObj.getMashupDataEditorSupport().synchDocument();
                    mObj.getGraphManager().refreshGraph();
                }
            } else if(type.equals("Materialized View") ||
                    type.equals("Union") || type.equals("Intersect")) {
                // create view widget on the canvas.
                NotifyDescriptor d =
                        new NotifyDescriptor.Message("Operator not supported.",
                        NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
        } catch (Exception ex) {
            manager.setLog("Error adding operator.");
        }
    }
}