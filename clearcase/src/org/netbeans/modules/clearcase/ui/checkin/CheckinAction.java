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
package org.netbeans.modules.clearcase.ui.checkin;

import java.awt.BorderLayout;
import javax.swing.event.TableModelEvent;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.VersioningOutputManager;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.versioning.util.DialogBoundsPreserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

import javax.swing.event.TableModelListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.clearcase.*;
import org.netbeans.modules.clearcase.ui.add.AddAction;
import org.netbeans.modules.clearcase.client.ExecutionUnit;
import org.netbeans.modules.clearcase.client.OutputWindowNotificationListener;
import org.netbeans.modules.clearcase.client.CheckinCommand;
import org.netbeans.modules.clearcase.client.NotificationListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

/**
 * Sample Update action.
 * 
 * @author Maros Sandor
 */
public class CheckinAction extends AbstractAction implements NotificationListener {
    
    private final VCSContext context;
    protected final VersioningOutputManager voutput;
    
    static int ALLOW_CHECKIN = 
            FileInformation.STATUS_VERSIONED_CHECKEDOUT |
            FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY;
    
    private File[] files;
    private RequestProcessor.Task prepareTask;
    
    public CheckinAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
        voutput = VersioningOutputManager.getInstance();
    }
    
    @Override
    public boolean isEnabled() {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        Set<File> roots = context.getRootFiles();        
        for (File root : roots) {
            if(root.isDirectory()) {
                return true;
            }
            FileInformation info = cache.getCachedInfo(root);            
            if(info != null && ((info.getStatus() & ALLOW_CHECKIN) != 0)) {
                return true;
            }
        }
        return false;
    }
    
    public void actionPerformed(ActionEvent ev) {
        String contextTitle = Utils.getContextDisplayName(context);
        final JButton addButton = new JButton(); 
        addButton.setEnabled(false);
        final JButton cancelButton = new JButton("Cancel");         
        
        CheckinPanel panel = new CheckinPanel();        
        
        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(CheckinAction.class, "CTL_CheckinDialog_Title", contextTitle)); // NOI18N
        dd.setModal(true);        
        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CheckinAction.class, "CTL_CheckinDialog_Checkin"));
        
        dd.setOptions(new Object[] {addButton, cancelButton}); // NOI18N
        dd.setHelpCtx(new HelpCtx(CheckinAction.class));

        final CheckinTable checkinTable = new CheckinTable(panel.jLabel2, CheckinTable.CHECKIN_COLUMNS, new String [] { CheckinTableModel.COLUMN_NAME_NAME });        
        panel.setCheckinTable(checkinTable);
        checkinTable.getTableModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                addButton.setEnabled(checkinTable.getTableModel().getRowCount() > 0);
            }
        });
        computeNodes(checkinTable, cancelButton, panel);
        
        panel.putClientProperty("contentTitle", contextTitle);  // NOI18N
        panel.putClientProperty("DialogDescriptor", dd); // NOI18N
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);        
        dialog.addWindowListener(new DialogBoundsPreserver(ClearcaseModuleConfig.getPreferences(), "add.dialog")); // NOI18N       
        dialog.pack();        
        dialog.setVisible(true);                
                
        Object value = dd.getValue();
        if (value != addButton) return;                 
        
        String message = panel.taMessage.getText();
        boolean forceUnmodified = panel.cbForceUnmodified.isSelected();
        boolean preserveTime = panel.cbPreserveTime.isSelected();

        Map<ClearcaseFileNode, CheckinOptions> filesToCheckin = checkinTable.getAddFiles();

        AddAction.addFiles(message, false, filesToCheckin);
        
        // TODO: process options
        List<File> ciFiles = new ArrayList<File>(); 
        for (Map.Entry<ClearcaseFileNode, CheckinOptions> entry : filesToCheckin.entrySet()) {
            if (entry.getValue() != CheckinOptions.EXCLUDE) {
                ciFiles.add(entry.getKey().getFile());
            }
        }
        files = ciFiles.toArray(new File[ciFiles.size()]);
        Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Checking in...",
                new CheckinCommand(files, message, forceUnmodified, 
                                    preserveTime, new OutputWindowNotificationListener(), this)));
    }

    // XXX temporary solution...
    private void computeNodes(final CheckinTable checkinTable, JButton cancel, final CheckinPanel checkinPanel) {
        RequestProcessor rp = new RequestProcessor("Clearcase-Checkin");
        final Cancellable c = new Cancellable() {            
            public boolean cancel() {
                // XXX doesn't realy work ...
                if(prepareTask != null) {
                    return prepareTask.cancel();
                }
                return false;
            }
        };                
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.cancel();
            }
        });
        if(prepareTask == null) {
            final ProgressHandle ph = ProgressHandleFactory.createHandle("Preparing checkin...", c);            
            JComponent bar = ProgressHandleFactory.createProgressComponent(ph);                                        
            checkinPanel.barPanel.add(bar, BorderLayout.CENTER);        
            prepareTask = rp.create(new Runnable() {
                public void run() {
                    try {
                        checkinPanel.progressPanel.setVisible(true);
                        ph.start();
                        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();

                        // refresh the cache first so we will
                        // know all checkin candidates
                        cache.refreshRecursively(context);
                        
                        // get all files to be checked in
                        File [] files = cache.listFiles(context, FileInformation.STATUS_LOCAL_CHANGE);
                        List<ClearcaseFileNode> nodes = new ArrayList<ClearcaseFileNode>(files.length);
                        for (File file : files) {
                            nodes.add(new ClearcaseFileNode(file));   
                        }                            
                        ClearcaseFileNode[] fileNodes = nodes.toArray(new ClearcaseFileNode[nodes.size()]);
                        checkinTable.setNodes(fileNodes);
                    } finally {
                        ph.finish();
                        checkinPanel.progressPanel.setVisible(false);
                    }
                }
            });        
        }
        prepareTask.schedule(0);
    }

    /**
     * Programmatically invoke the checkin action on some context.
     * 
     * @param context a context to check in
     */
    public static void checkin(VCSContext context) {
        new CheckinAction("", context).actionPerformed(null);        
    }
    
    public void commandStarted()        { /* boring */ }
    public void outputText(String line) { /* boring */ }
    public void errorText(String line)  { /* boring */ }
    public void commandFinished() {               
        org.netbeans.modules.clearcase.util.Utils.afterCommandRefresh(files, false);        
    }    
}
