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
package org.netbeans.modules.edm.editor.graph.actions;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.components.EDMOutputTopComponent;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.edm.editor.graph.components.AddTablePanel;
import org.netbeans.modules.edm.editor.utils.MashupModelHelper;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * This class implements the action for adding new table.
 * @author Nithya
 */
public class AddTableAction extends AbstractAction {
    
    /**
     * member variable for mashup data object.
     */
    private MashupDataObject mObj;
    
    /**
     * Implements actionPerformed. 
     */
    public void actionPerformed(ActionEvent e) {
        JLabel panelTitle = new JLabel("Select Source Tables");
        panelTitle.setFont(panelTitle.getFont().deriveFont(Font.BOLD));
        panelTitle.setFocusable(false);
        panelTitle.setHorizontalAlignment(SwingConstants.LEADING);
        AddTablePanel addPanel = new AddTablePanel(mObj);
        
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panelTitle, BorderLayout.NORTH);
        contentPane.add(addPanel, BorderLayout.CENTER);
        
        
        DialogDescriptor dd = new DialogDescriptor(contentPane, NbBundle.getMessage(EDMOutputTopComponent.class, "TITLE_select_source_tables"));
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setSize(new Dimension(600, 450));
        dlg.setVisible(true);
        if (NotifyDescriptor.OK_OPTION.equals(dd.getValue())) {
            mObj.getMashupDataEditorSupport().synchDocument();
            MashupModelHelper.getModel(mObj.getModel(), addPanel.getTables());
            mObj.getGraphManager().refreshGraph();
        }
    }
    
    public AddTableAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.ADDTABLE)));
        mObj = dObj;
    }
    
    public AddTableAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.ADDTABLE)));
        mObj = dObj;
    }    
}