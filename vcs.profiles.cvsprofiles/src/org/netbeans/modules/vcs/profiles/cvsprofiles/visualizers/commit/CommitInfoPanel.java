/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit;

import java.awt.FontMetrics;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.table.*;
import org.netbeans.api.vcs.commands.*;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update.GrowingTableInfoModel;
import org.netbeans.modules.vcscore.commands.CommandOutputVisualizer;
import org.netbeans.modules.vcscore.ui.*;
import org.netbeans.modules.vcscore.util.table.*;
import org.openide.util.*;

/**
 * CommitInfoPanel.java
 *
 * Created on December 21, 2003, 7:42 PM
 * @author  Richard Gregor
 */
public class CommitInfoPanel extends AbstractOutputPanel{
    private JTable tblCommit;
    private JTextArea errOutput;
    private GrowingTableInfoModel model;
    private CommandTask task;
    long currentTimeStamp;
    long firedTimeStamp = 0;
    int addedCount = 0;
    int totalCount = 0;
    int lastSelection = -1;
    int lastHBar = 0;
    private OutputVisualizer visualizer;
    
    
    public CommitInfoPanel(OutputVisualizer visualizer) {
        super();
        this.visualizer = visualizer;
        getAccessibleContext().setAccessibleName(NbBundle.getBundle(CommitInfoPanel.class).getString("ACS_CommitInfoPanel")); //NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CommitInfoPanel.class).getString("ACSD_CommitInfoPanel")); //NOI18N
    }
    
    protected boolean isViewTextLogEnabled() {
        return true;
    }
    
    protected JComponent getErrComponent() {
        if(errOutput == null){
            errOutput = new JTextArea();
            errOutput.setEditable(false);
            errOutput.getAccessibleContext().setAccessibleName(NbBundle.getBundle(CommitInfoPanel.class).getString("ACS_CommitInfoPanel.errOutput")); //NOI18N
            errOutput.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CommitInfoPanel.class).getString("ACSD_CommitInfoPanel.errOutput")); //NOI18N
            java.awt.Font font = errOutput.getFont();
            errOutput.setFont(new java.awt.Font("Monospaced", font.getStyle(), font.getSize()));
        }
        return errOutput;
    }
    
    protected JComponent getStdComponent(){
        if(tblCommit == null){
            tblCommit = new JTable();
            tblCommit.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
            ));
            model = new GrowingTableInfoModel();
            Class classa = CommitInformation.class;
            String column1 = NbBundle.getBundle(CommitInfoPanel.class).getString("CommitTableInfoModel.type"); //NOI18N
            String column2 = NbBundle.getBundle(CommitInfoPanel.class).getString("CommitTableInfoModel.fileName"); //NOI18N
            String column3 = NbBundle.getBundle(CommitInfoPanel.class).getString("CommitTableInfoModel.revision"); //NOI18N
            String column4 = NbBundle.getBundle(CommitInfoPanel.class).getString("CommitTableInfoModel.path"); //NOI18N
            try {
                Method method1 = classa.getMethod("getType", null);     //NOI18N
                Method method2 = classa.getMethod("getFile", null);     //NOI18N
                Method method3 = classa.getMethod("getRevision", null);     //NOI18N
                model.setColumnDefinition(0, column1, method1, true, null);                
                model.setColumnDefinition(1, column2, method2, true, new FileComparator());
                model.setColumnDefinition(2, column3, method3, true, new RevisionComparator());
                model.setColumnDefinition(3, column4, method2, true, null);
            } catch (NoSuchMethodException exc) {
                Thread.dumpStack();
            } catch (SecurityException exc2) {
                Thread.dumpStack();
            }
            tblCommit.setModel(model);
            TableColumn col = tblCommit.getColumnModel().getColumn(2);
            col.setMaxWidth(60);            
            col = tblCommit.getColumnModel().getColumn(0);
            col.setMaxWidth(60);
            tblCommit.getAccessibleContext().setAccessibleName(NbBundle.getBundle(CommitInfoPanel.class).getString("CommitInfoPanel.table")); //NOI18N
            tblCommit.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CommitInfoPanel.class).getString("ACSD_CommitInfoPanel.table")); //NOI18N        
        }
        return tblCommit;
    }
    
    protected boolean isErrOutput() {
        return (errOutput.getText().length() > 0);
    }
    
    protected boolean isStdOutput() {
        return (tblCommit.getModel().getRowCount() > 0);
    }
    

    public void setVcsTask(CommandTask task){
        this.task = task;
        addKillActionListener(new CommandOutputVisualizer.CommandKillListener(task));
    }
    
    /** Does the actual display - docking into the javacvs Mode,
     *  displaying as single Dialog.. whatever.
     */
    private void displayOutputData() {
        JTableHeader head = tblCommit.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblCommit);
        head.addMouseListener(listen);
    }
    
    
    public void showFinishedCommand(int exit) {
        displayOutputData();
        commandFinished(exit);
    }
    
    public void showStartCommand() {
        displayFrameWork();
    }
    
    public void displayFrameWork() {
        tblCommit.setModel(model);
        TableColumn col = tblCommit.getColumnModel().getColumn(0);        
        col.setPreferredWidth(60);
    }
    
    public void showFileInfoGenerated(CommitInformation info) {        
        if (info instanceof CommitInformation) {
            model.addElement(info);            
            currentTimeStamp = System.currentTimeMillis();
            addedCount = addedCount + 1;
            totalCount = totalCount + 1;
            long tpDiff = currentTimeStamp - firedTimeStamp;
            if (totalCount < 100 || (addedCount > 5 && tpDiff > 500)) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tblCommit.changeSelection(model.getRowCount(), 0, false, false);
                    }
                });
                firedTimeStamp = System.currentTimeMillis();
                addedCount = 0;
            }
        }
    }
    
}
