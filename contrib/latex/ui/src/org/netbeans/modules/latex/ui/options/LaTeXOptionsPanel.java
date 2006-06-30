/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.options;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.latex.ui.Autodetector;
import org.netbeans.modules.latex.ui.IconsStorageImpl;
import org.netbeans.modules.latex.ui.ModuleSettings;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXOptionsPanel extends javax.swing.JPanel {
    
    private static final int SCHEDULE = 1000;
    
    private RequestProcessor PROCESSING = new RequestProcessor("LaTeXOptionsPanel", 1);
    private Map<String, RequestProcessor.Task> tasks = new HashMap<String, RequestProcessor.Task>();
    private Map<String, JTextField> program2Field = new HashMap<String, JTextField>();

    private static final List<String> programs = Arrays.asList(new String[] {
            "latex",
            "bibtex",
            "dvips",
            "ps2pdf",
            "gs",
            "xdvi",
            "gv",
        });
    
    /** Creates new form LaTeXOptionsPanel */
    public LaTeXOptionsPanel() {
        initComponents();

        jPanel3.add(createPanel(programs));
    }

    private static Map<String, String> program2Name;

    static {
        program2Name = new HashMap<String, String>();

        program2Name.put("latex", "latex:");
        program2Name.put("bibtex", "bibtex:");
        program2Name.put("dvips", "dvips:");
        program2Name.put("ps2pdf", "ps2pdf:");
        program2Name.put("gs", "gs:");
        program2Name.put("xdvi", "DVI viewer:");
        program2Name.put("gv", "PS/PDF viewer:");
    }

    private JPanel createPanel(List<String> programs) {
        JPanel result = new JPanel();

        result.setLayout(new GridBagLayout());

        int index = 0;

        for (String p : programs) {
            GridBagConstraints c;

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2 * index;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(0, 0, 6, 6);

            result.add(new JLabel(program2Name.get(p)), c);

            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 2 * index;
            c.weightx = 1.0;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(0, 0, 6, 6);

            final JTextField command = new JTextField();

            result.add(command, c);
            program2Field.put(p, command);

            c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 2 * index;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(0, 0, 6, 0);

            JButton b = new JButton("Browse");

            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    browse(command);
                }
            });

            result.add(b, c);

            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 2 * index + 1;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(0, 0, 9, 0);

            JLabel working = new JLabel("Working...");

            result.add(working, c);

            tasks.put(p, PROCESSING.create(new CheckProgram(p, command, working)));

            invalidate(tasks.get(p), working);

            index++;
        }

        return result;
    }

    private void browse(JTextField field) {
        File content = new File(field.getText());
        JFileChooser chooser = new JFileChooser();

        if (content.exists())
            chooser.setCurrentDirectory(content);

        chooser.setMultiSelectionEnabled(false);

        if (chooser.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void invalidate(RequestProcessor.Task task, final JLabel result) {
        task.schedule(SCHEDULE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                result.setText("Working...");
            }
        });
    }
    
    public void update() {
        Map settings = ModuleSettings.getDefault().readSettings();
        
        for (String p : programs) {
            JTextField f = program2Field.get(p);
            
            if (settings != null) {
                f.setText((String) settings.get(p));
            } else {
                f.setText("");
            }
        }
    }
    
    public void commit() {
        Map settings = ModuleSettings.getDefault().readSettings();
        
        if (settings == null) {
            settings = new HashMap();
        }
        
        //TODO: outside the AWT:
        for (String p : programs) {
            JTextField f = program2Field.get(p);
            String command = f.getText();

            settings.put(p, command);
            settings.put(p + "-quality", Boolean.valueOf(Autodetector.checkProgram(p, command) == Autodetector.OK));
        }
        
        ModuleSettings.getDefault().writeSettings(settings);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Commands", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        jPanel1.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Icons"));
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "Clear Icons Cache");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton1)
                .addContainerGap(682, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        NotifyDescriptor confirm = new NotifyDescriptor.Confirmation("Do you really wish the clear icons cache? All icons will be recreated on demand.");

        if (DialogDisplayer.getDefault().notify(confirm) == NotifyDescriptor.YES_OPTION) {
            if (!IconsStorageImpl.getDefaultImpl().clearIconsCache()) {
                NotifyDescriptor error = new NotifyDescriptor.Message("Cannot clear icons cache.", NotifyDescriptor.ERROR_MESSAGE);

                DialogDisplayer.getDefault().notify(error);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
    
    private final class CheckProgram implements Runnable, DocumentListener {
        
        private String type;
        private JTextField command;
        private JLabel result;
        
        public CheckProgram(String type, JTextField command, JLabel result) {
            this.type = type;
            this.command = command;
            this.result = result;

            command.getDocument().addDocumentListener(this);
        }
        
        public void run() {
//            System.err.println("check program started for type: " + type);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    result.setText("Working...");
                }
            });
            
            int checkResult = Autodetector.checkProgram(type, command.getText());
            String text = "Unknown status";
            
            switch (checkResult) {
                case Autodetector.NOT_FOUND:
                    text = "Command not found.";
                    break;
                case Autodetector.NOT_CONTENT:
                    text = "Command found, but has an incompatible version.";
                    break;
                case Autodetector.OK:
                    text = "Command found.";
                    break;
            }
            
            final String textFin = text;
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    result.setText(textFin);
                }
            });
//            System.err.println("check program finished for type: " + type);
        }

        public void changedUpdate(DocumentEvent e) {
        }

        public void insertUpdate(DocumentEvent e) {
            invalidate(tasks.get(type), result);
        }

        public void removeUpdate(DocumentEvent e) {
            invalidate(tasks.get(type), result);
        }

    }

}
