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
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.ui.editor.property.IPropertySheet;
import org.netbeans.modules.etl.ui.view.DBModelTreeView;
import org.netbeans.modules.etl.ui.view.ETLCollaborationTopComponent;
import org.netbeans.modules.etl.ui.view.EditDBModelPanel;

/**
 *
 * @author karthikeyan s
 */
public class EditConnectionAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public EditConnectionAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITCONNECTION)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
    }
    
    public EditConnectionAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.EDITCONNECTION)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
    }
    
    public void actionPerformed(ActionEvent e) {        
        JLabel panelTitle = new JLabel("Edit Database Properties");
        panelTitle.setFont(panelTitle.getFont().deriveFont(Font.BOLD));
        panelTitle.setFocusable(false);
        panelTitle.setHorizontalAlignment(SwingConstants.LEADING);
        EditDBModelPanel editPanel = new EditDBModelPanel(mObj.getModel());
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panelTitle, BorderLayout.NORTH);
        contentPane.add(editPanel, BorderLayout.CENTER);
        
        
        DialogDescriptor dd = new DialogDescriptor(contentPane, NbBundle.getMessage(ETLCollaborationTopComponent.class, "TITLE_edit_database_properties"));
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setSize(new Dimension(600, 450));
        dlg.setVisible(true);
        if (NotifyDescriptor.OK_OPTION.equals(dd.getValue())) {
            DBModelTreeView dbModelTreeView = editPanel.getDBModelTreeView();
            if (dbModelTreeView != null) {
                IPropertySheet propSheet = dbModelTreeView.getPropSheet();
                if (propSheet != null) {
                    propSheet.commitChanges();
                    mObj.getMashupDataEditorSupport().synchDocument();
                    manager.setLog("Database Connection properties successfully modified.");
                }
            }
        }    
    }
}