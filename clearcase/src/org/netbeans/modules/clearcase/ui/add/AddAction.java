/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.clearcase.ui.add;

import java.awt.event.ActionEvent;
import java.awt.Dialog;
import java.io.File;
import java.util.*;
import javax.swing.*;

import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.VersioningOutputManager;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.versioning.util.DialogBoundsPreserver;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;
import org.netbeans.modules.clearcase.ClearcaseFileNode;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.FileInformation;
import org.netbeans.modules.clearcase.ui.checkin.CheckinOptions;
import org.netbeans.modules.clearcase.client.ExecutionUnit;
import org.netbeans.modules.clearcase.client.OutputWindowNotificationListener;
import org.netbeans.modules.clearcase.client.MkElemCommand;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;

/**
 * Sample Update action.
 * 
 * @author Maros Sandor
 */
public class AddAction extends AbstractAction {
    
    static final String RECENT_ADD_MESSAGES = "add.messages";

    private final VCSContext context;
    protected final VersioningOutputManager voutput;

    public AddAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
        voutput = VersioningOutputManager.getInstance();
    }
    
    @Override
    public boolean isEnabled() {
        // TODO
        return true;
    }
    
    public void actionPerformed(ActionEvent ev) {
        String contextTitle = Utils.getContextDisplayName(context);
        JButton addButton = new JButton(); 
        AddPanel panel = new AddPanel();
        
        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(AddAction.class, "CTL_AddDialog_Title", contextTitle)); // NOI18N
        dd.setModal(true);        
        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(AddAction.class, "CTL_AddDialog_Add"));
        
        dd.setOptions(new Object[] {addButton, DialogDescriptor.CANCEL_OPTION}); // NOI18N
        dd.setHelpCtx(new HelpCtx(AddAction.class));

        AddTable addTable = new AddTable(panel.jLabel2, AddTable.ADD_COLUMNS, new String [] { AddTableModel.COLUMN_NAME_NAME });
        ClearcaseFileNode [] nodes = computeNodes();
        addTable.setNodes(nodes);
        panel.setAddTable(addTable);
        
        panel.putClientProperty("contentTitle", contextTitle);  // NOI18N
        panel.putClientProperty("DialogDescriptor", dd); // NOI18N
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);        
        dialog.addWindowListener(new DialogBoundsPreserver(ClearcaseModuleConfig.getPreferences(), "add.dialog")); // NOI18N       
        dialog.pack();        
        dialog.setVisible(true);
        
        Object value = dd.getValue();
        if (value != addButton) return;
        
        String message = panel.taMessage.getText();
        boolean checkInAddedFiles = panel.cbSuppressCheckout.isSelected();

        Map<ClearcaseFileNode, CheckinOptions> filesToAdd = addTable.getAddFiles();
        
        // TODO: process options
        List<File> files = new ArrayList<File>(); 
        for (Map.Entry<ClearcaseFileNode, CheckinOptions> entry : filesToAdd.entrySet()) {
            if (entry.getValue() != CheckinOptions.EXCLUDE_FROM_ADD) {
                files.add(entry.getKey().getFile());
            }
        }
        
        // sort files - parents first, to avoid unnecessary warnings
        Collections.sort(files);

        Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Adding...",
                new MkElemCommand(files.toArray(new File[files.size()]), message, checkInAddedFiles ? MkElemCommand.Checkout.Checkin : MkElemCommand.Checkout.Default, 
                                    false, new OutputWindowNotificationListener())));
    }

    private ClearcaseFileNode[] computeNodes() {
        File [] files = Clearcase.getInstance().getFileStatusCache().listFiles(context, FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY);
        List<ClearcaseFileNode> nodes = new ArrayList<ClearcaseFileNode>(files.length);
        for (File file : files) {
            nodes.add(new ClearcaseFileNode(file));
        }
        return nodes.toArray(new ClearcaseFileNode[nodes.size()]);
    }
}
