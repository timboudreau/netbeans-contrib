/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * PackagerCustomizer.java
 *
 * Created on May 26, 2004, 3:40 AM
 */

package org.netbeans.modules.packager.ui;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.packager.PackagerProject;
import org.netbeans.modules.project.ant.Util;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Customizer to allow selection of included projects.
 *
 * @author  Tim Boudreau
 */
public class PackagerCustomizer extends javax.swing.JPanel implements java.awt.event.FocusListener, java.awt.event.ActionListener, javax.swing.event.ListSelectionListener, java.awt.event.KeyListener, java.beans.PropertyChangeListener {
    private CustomizerAsWizardPanel panel;
    private PackagerCustomizer() {
        initComponents();
        included.setModel(new ProjectListModel());
        nameKeyTyped(null);
        included.setCellRenderer(new ProjectRenderer());
    }
    
    public PackagerCustomizer(CustomizerAsWizardPanel panel) {
        this();
        this.panel = panel;
    }
    
    public PackagerCustomizer(PackagerProject project) {
        this();
        SubprojectProvider prov = (SubprojectProvider) project.getLookup().lookup (SubprojectProvider.class);
        Set subs = prov.getSubProjects();
        for (Iterator i=subs.iterator(); i.hasNext();) {
            ((ProjectListModel) included.getModel()).add((Project) i.next());
        }
        name.setText (ProjectUtils.getInformation(project).getDisplayName());
        
        dir.setText (project.getProjectDirectory().getPath());
        dir.setEnabled(false);
        choosedir.setEnabled(false);
        name.setEnabled(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        namelabel = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        add = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        included = new javax.swing.JList();
        includedlabel = new javax.swing.JLabel();
        platformspanel = new javax.swing.JPanel();
        windows = new javax.swing.JCheckBox();
        mac = new javax.swing.JCheckBox();
        unix = new javax.swing.JCheckBox();
        webstart = new javax.swing.JCheckBox();
        dirlabel = new javax.swing.JLabel();
        dir = new javax.swing.JTextField();
        choosedir = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(11, 11, 11, 11)));
        namelabel.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_ProjectName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        add(namelabel, gridBagConstraints);

        name.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_NewProject"));
        name.addFocusListener(this);
        name.addKeyListener(this);
        name.addPropertyChangeListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(name, gridBagConstraints);

        add.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Add"));
        add.addActionListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(add, gridBagConstraints);

        remove.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Remove"));
        remove.addActionListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(remove, gridBagConstraints);

        included.addListSelectionListener(this);

        jScrollPane1.setViewportView(included);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        add(jScrollPane1, gridBagConstraints);

        includedlabel.setLabelFor(included);
        includedlabel.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Included"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 3);
        add(includedlabel, gridBagConstraints);

        platformspanel.setBorder(new javax.swing.border.TitledBorder(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Platforms")));
        windows.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Windows"));
        windows.setEnabled(false);
        windows.addActionListener(this);

        platformspanel.add(windows);

        mac.setSelected(true);
        mac.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Mac"));
        mac.setEnabled(false);
        mac.addActionListener(this);

        platformspanel.add(mac);

        unix.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_Unix"));
        unix.setEnabled(false);
        unix.addActionListener(this);

        platformspanel.add(unix);

        webstart.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_WebStart"));
        webstart.setEnabled(false);
        platformspanel.add(webstart);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 267;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        add(platformspanel, gridBagConstraints);

        dirlabel.setLabelFor(dir);
        dirlabel.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_ProjectDir"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        add(dirlabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(dir, gridBagConstraints);

        choosedir.setText(NbBundle.getMessage(PackagerCustomizer.class,"LBL_ChooseDir"));
        choosedir.addActionListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(choosedir, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == add) {
            PackagerCustomizer.this.addActionPerformed(evt);
        }
        else if (evt.getSource() == remove) {
            PackagerCustomizer.this.removeActionPerformed(evt);
        }
        else if (evt.getSource() == windows) {
            PackagerCustomizer.this.windowsActionPerformed(evt);
        }
        else if (evt.getSource() == mac) {
            PackagerCustomizer.this.unixActionPerformed(evt);
        }
        else if (evt.getSource() == unix) {
            PackagerCustomizer.this.unixActionPerformed(evt);
        }
        else if (evt.getSource() == choosedir) {
            PackagerCustomizer.this.choosedirActionPerformed(evt);
        }
    }

    public void focusGained(java.awt.event.FocusEvent evt) {
        if (evt.getSource() == name) {
            PackagerCustomizer.this.nameFocusGained(evt);
        }
    }

    public void focusLost(java.awt.event.FocusEvent evt) {
        if (evt.getSource() == name) {
            PackagerCustomizer.this.nameFocusLost(evt);
        }
    }

    public void keyPressed(java.awt.event.KeyEvent evt) {
    }

    public void keyReleased(java.awt.event.KeyEvent evt) {
    }

    public void keyTyped(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == name) {
            PackagerCustomizer.this.nameKeyTyped(evt);
        }
    }

    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getSource() == name) {
            PackagerCustomizer.this.namePropertyChange(evt);
        }
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (evt.getSource() == included) {
            PackagerCustomizer.this.includedValueChanged(evt);
        }
    }//GEN-END:initComponents

    private void choosedirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choosedirActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser (ProjectChooser.getProjectsFolder());
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.showDialog(this, NbBundle.getMessage(PackagerCustomizer.class, "LBL_SelectDir")) == jfc.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            dir.setText(f != null ? f.getPath() : "");
        }
    }//GEN-LAST:event_choosedirActionPerformed

    private void namePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_namePropertyChange
        // TODO add your handling code here:
        if ("text".equals(evt.getPropertyName())) {
            nameKeyTyped(null);
        }
    }//GEN-LAST:event_namePropertyChange

    private void windowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowsActionPerformed
        // TODO add your handling code here:
        fire();
    }//GEN-LAST:event_windowsActionPerformed

    private void unixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unixActionPerformed
        // TODO add your handling code here:
        fire();
    }//GEN-LAST:event_unixActionPerformed

    private void nameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFocusLost
        // TODO add your handling code here:
        fire();
    }//GEN-LAST:event_nameFocusLost

    private void nameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String s = name.getText();
                String home = System.getProperty("user.home"); //NOI18N
                String currdir = dir.getText().trim();
                if (currdir.indexOf(home) != -1 || currdir.length()==0) {
                    String nue = home + File.separator + s;
                    dir.setText(nue);
                }
                fire();
            }
        });
    }//GEN-LAST:event_nameKeyTyped

    private void includedValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_includedValueChanged
        int[] idxs = included.getSelectedIndices();
        boolean enable = 
            idxs != null && 
            idxs.length > 0;
        
        if (enable) {
            for (int i=0; i < idxs.length; i++) {
                enable &= !((ProjectListModel) included.getModel()).isDependency(idxs[i]);
                if (!enable) break;
            }
                
        }
        remove.setEnabled(enable);
    }//GEN-LAST:event_includedValueChanged

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        // TODO add your handling code here:
        int[] indices = included.getSelectedIndices();
        if (indices.length > 0) {
            Arrays.sort(indices);
            for (int i=indices.length-1; i >= 0; i--) {
                ((ProjectListModel)included.getModel()).remove(indices[i]);
            }
            included.repaint();
            fire();
        }
    }//GEN-LAST:event_removeActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        JFileChooser jfc = ProjectChooser.projectChooser();
        if (jfc.showDialog(this, NbBundle.getMessage(PackagerCustomizer.class, 
            "LAB_ConfigureProject")) == jfc.APPROVE_OPTION) { //NOI18N
                
            File f = jfc.getSelectedFile();
            if (f != null) {
                Project p = FileOwnerQuery.getOwner(f.toURI());
                if (p != null) {
                    ((ProjectListModel)included.getModel()).add (p);
                }
            }
        }
        fire();
    }//GEN-LAST:event_addActionPerformed

    private void nameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFocusGained
        name.setSelectionStart(0);
        name.setSelectionEnd(name.getText().length());
        
    }//GEN-LAST:event_nameFocusGained
    
    private void fire() {
        if (panel != null) {
            panel.fireChangeEvent();
        }
    }
    
    boolean valid( WizardDescriptor wizardDescriptor ) {
        Project[] p = getChildProjects();
        if (name.getText().trim().length() == 0) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                NbBundle.getMessage(PackagerCustomizer.class,"MSG_NoProjectName"));
            return false;
        }

        boolean hasPlatform = 
            windows.isSelected() || mac.isSelected() || unix.isSelected()
            || webstart.isSelected();
        if (!hasPlatform) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                NbBundle.getMessage(PackagerCustomizer.class,"MSG_NoPlatform"));
            return false;
        }
        
        File destFolder = new File( dir.getText() );
        File[] kids = destFolder.listFiles();
        if (destFolder.exists() && kids != null && kids.length > 0) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                NbBundle.getMessage(PackagerCustomizer.class,"MSG_ProjectFolderExists"));
            return false;
        }
        
        if (included.getModel().getSize() == 0) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                NbBundle.getMessage(PackagerCustomizer.class,"MSG_NoChildProjects"));
            return false;
        }
        
        if (!((ProjectListModel) included.getModel()).hasExecutableProject()) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                NbBundle.getMessage(PackagerCustomizer.class,"MSG_NoMainClass"));

            return false;
        }
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", "" );
        
        return true;
    }
    
    public boolean isMac() {
        return mac.isSelected();
    }
    
    public boolean isWindows() {
        return windows.isSelected();
    }
    
    public boolean isWebStart() {
        return webstart.isSelected();
    }
    
    public boolean isUnix() {
        return unix.isSelected();
    }
    
    public void store( WizardDescriptor d ) {        
        
        String nm = name.getText().trim();
        String location = dir.getText().trim();
        
        
        d.putProperty(KEY_DIR, new File( location )); // NOI18N
        d.putProperty(KEY_NAME, nm ); // NOI18N  
        d.putProperty(KEY_PROJECTS, getDependentProjects());
        d.putProperty(KEY_MAC, isMac() ? Boolean.TRUE : Boolean.FALSE);
        d.putProperty(KEY_UNIX, isUnix() ? Boolean.TRUE : Boolean.FALSE);
        d.putProperty(KEY_WINDOWS, isWindows() ? Boolean.TRUE : Boolean.FALSE);
        d.putProperty(KEY_WEBSTART, isWebStart() ? Boolean.TRUE : Boolean.FALSE);
        
        File projectsDir = new File(location);
        if (projectsDir.isDirectory()) {
            ProjectChooser.setProjectsFolder (projectsDir);
        }
    }
    
    public void read ( WizardDescriptor d ) {
        File projdir = (File) d.getProperty(KEY_DIR);
        String nm = (String) d.getProperty(KEY_NAME);
        Project[] kids = (Project[]) d.getProperty(KEY_PROJECTS);
        if (projdir != null) {
            dir.setText(projdir.getPath());
        }
        if (name != null) {
            name.setText(nm);
        }
        if (kids != null) {
            for (int i=0; i < kids.length; i++) {
                ((ProjectListModel) included.getModel()).add(kids[i]);
            }
        }
        unix.setSelected (Boolean.TRUE.equals(d.getProperty(KEY_UNIX)));
        webstart.setSelected (Boolean.TRUE.equals(d.getProperty(KEY_WEBSTART)));
        mac.setSelected (Boolean.TRUE.equals(d.getProperty(KEY_MAC)));
        windows.setSelected (Boolean.TRUE.equals(d.getProperty(KEY_WINDOWS)));
        
        mac.setSelected (true); //XXX for now
    }
    
    public static final String KEY_NAME = "name"; //NOI18N
    public static final String KEY_DIR = "projdir"; //NOI18N
    public static final String KEY_PROJECTS = "childProjects"; //NOI18N
    public static final String KEY_MAC = "mac"; //NOI18N
    public static final String KEY_UNIX = "unix"; //NOI18N
    public static final String KEY_WINDOWS = "windows"; //NOI18N
    public static final String KEY_WEBSTART = "webstart"; //NOI18N
    
    public String getName() {
        return NbBundle.getMessage (PackagerCustomizer.class, "LAB_ConfigureProject"); //NOI18N
    }
    
    public File getDir() {
        File f = new File (dir.getText());
        if (!f.exists()) {
            return f;
        }
        return null;
    }
    
    public Project[] getChildProjects() {
        return ((ProjectListModel) included.getModel()).getProjects();
    }
    
    public Project[] getDependentProjects() {
        return ((ProjectListModel) included.getModel()).getAllProjects();
    }
    
    
    private class ProjectRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list,  Object value,  int index, boolean isSelected,  boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
            Project p = (Project) value;
            ProjectListModel mdl = (ProjectListModel) list.getModel();
            
            setText (ProjectUtils.getInformation(p).getDisplayName());
            setIcon (ProjectUtils.getInformation(p).getIcon());
            if (mdl.isExecutable(p)) {
                c.setFont(list.getFont().deriveFont(Font.BOLD));
            } else {
                c.setFont(list.getFont());
            }
            c.setEnabled (!mdl.isDependency(p));
            return c;
        }
    }
 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton choosedir;
    private javax.swing.JTextField dir;
    private javax.swing.JLabel dirlabel;
    private javax.swing.JList included;
    private javax.swing.JLabel includedlabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox mac;
    private javax.swing.JTextField name;
    private javax.swing.JLabel namelabel;
    private javax.swing.JPanel platformspanel;
    private javax.swing.JButton remove;
    private javax.swing.JCheckBox unix;
    private javax.swing.JCheckBox webstart;
    private javax.swing.JCheckBox windows;
    // End of variables declaration//GEN-END:variables
    
}
