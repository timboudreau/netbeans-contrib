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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.add;

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
 * AddInfoPanel.java
 *
 * Created on December 21, 2003, 7:42 PM
 * @author  Richard Gregor
 */
public class AddInfoPanel extends AbstractOutputPanel{
    private JTable tblAdded;
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
    
    
    public AddInfoPanel(OutputVisualizer visualizer) {
        super();
        this.visualizer = visualizer;
    }
    
    protected boolean isViewTextLogEnabled() {
        return true;
    }
    
    protected JComponent getErrComponent() {
        if(errOutput == null){
            errOutput = new JTextArea();
            errOutput.setEditable(false);
        }
        return errOutput;
    }
    
    protected JComponent getStdComponent(){
        if(tblAdded == null){
            tblAdded = new JTable();
            tblAdded.setModel(new javax.swing.table.DefaultTableModel(
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
            Class classa = AddInformation.class;
            String  column1 = NbBundle.getBundle(AddInfoPanel.class).getString("AddTableInfoModel.type"); //NOI18N
            String  column2 = NbBundle.getBundle(AddInfoPanel.class).getString("AddTableInfoModel.fileName"); //NOI18N
            String  column3 = NbBundle.getBundle(AddInfoPanel.class).getString("AddTableInfoModel.path"); //NOI18N
            try {
                Method method1 = classa.getMethod("getType", null);    //NOI18N
                Method method2 = classa.getMethod("getFile", null);     //NOI18N
                model.setColumnDefinition(0, column1, method1, true, new AddTypeComparator());                
                model.setColumnDefinition(1, column2, method2, true, new FileComparator());
               // model.setColumnDefinition(2, column3, method2, true, new PathComparator(prov.getLocalPath()));
                model.setColumnDefinition(2, column3, method2, true, null);
            } catch (NoSuchMethodException exc) {
                Thread.dumpStack();
            } catch (SecurityException exc2) {
                Thread.dumpStack();
            }
        
        }
        return tblAdded;
    }
    
    protected boolean isErrOutput() {
        return (errOutput.getText().length() > 0);
    }
    
    protected boolean isStdOutput() {
        return (tblAdded.getModel().getRowCount() > 0);
    }
    

    public void setVcsTask(CommandTask task){
        this.task = task;
        addKillActionListener(new CommandOutputVisualizer.CommandKillListener(task));
    }
    
    /** Does the actual display - docking into the javacvs Mode,
     *  displaying as single Dialog.. whatever.
     */
    private void displayOutputData() {
        JTableHeader head = tblAdded.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblAdded);
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
        tblAdded.setModel(model);
        TableColumn col = tblAdded.getColumnModel().getColumn(0);
        col.setMaxWidth(80);        
    }
    
    public void showFileInfoGenerated(AddInformation info) {        
        if (info instanceof AddInformation) {
            model.addElement(info);            
            currentTimeStamp = System.currentTimeMillis();
            addedCount = addedCount + 1;
            totalCount = totalCount + 1;
            long tpDiff = currentTimeStamp - firedTimeStamp;
            if (totalCount < 100 || (addedCount > 5 && tpDiff > 500)) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tblAdded.changeSelection(model.getRowCount(), 0, false, false);
                    }
                });
                firedTimeStamp = System.currentTimeMillis();
                addedCount = 0;
            }
        }
    }
    
}
