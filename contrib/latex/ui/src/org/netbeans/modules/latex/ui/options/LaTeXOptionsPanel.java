/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.options;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.latex.editor.Dictionary;
import org.netbeans.modules.latex.ui.Autodetector;
import org.netbeans.modules.latex.ui.ModuleSettings;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXOptionsPanel extends javax.swing.JPanel {
    
    private static final int SCHEDULE = 1000;
    
    private RequestProcessor PROCESSING = new RequestProcessor("LaTeXOptionsPanel");
    private RequestProcessor.Task latexTask;
    private RequestProcessor.Task dvipsTask;
    private RequestProcessor.Task gsTask;
    
    /** Creates new form LaTeXOptionsPanel */
    public LaTeXOptionsPanel() {
        initComponents();
        
        latexTask = PROCESSING.create(new CheckProgram("latex", latexCommand, latexResult));
        dvipsTask = PROCESSING.create(new CheckProgram("dvips", dvipsCommand, dvipsResult));
        gsTask = PROCESSING.create(new CheckProgram("gs", gsCommand, gsResult));
        
        latexCommand.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                invalidate(latexTask, latexResult);
            }
            public void removeUpdate(DocumentEvent e) {
                invalidate(latexTask, latexResult);
            }
        });
        
        dvipsCommand.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                invalidate(dvipsTask, dvipsResult);
            }
            public void removeUpdate(DocumentEvent e) {
                invalidate(dvipsTask, dvipsResult);
            }
        });
        
        gsCommand.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                invalidate(gsTask, gsResult);
            }
            public void removeUpdate(DocumentEvent e) {
                invalidate(gsTask, gsResult);
            }
        });
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
        
        if (settings != null) {
            latexCommand.setText((String) settings.get("latex"));
            dvipsCommand.setText((String) settings.get("dvips"));
            gsCommand.setText((String) settings.get("gs"));
        } else {
            latexCommand.setText("");
            dvipsCommand.setText("");
            gsCommand.setText("");
        }
        
        DefaultListModel model = new DefaultListModel();
        Locale[] locales = Dictionary.getInstalledDictionariesLocales();
        
        for (int cntr = 0; cntr < locales.length; cntr++) {
            model.addElement(locales[cntr]);
        }
        
        installedLocalesList.setModel(model);
    }
    
    public void commit() {
        Map settings = ModuleSettings.getDefault().readSettings();
        
        if (settings == null) {
            settings = new HashMap();
        }
        
        //TODO: outside the AWT:
        settings.put("latex", latexCommand.getText());
        settings.put("latex-quality", Boolean.valueOf(Autodetector.checkProgram("latex", latexCommand.getText()) == Autodetector.OK));
        settings.put("dvips", dvipsCommand.getText());
        settings.put("dvips-quality", Boolean.valueOf(Autodetector.checkProgram("dvips", dvipsCommand.getText()) == Autodetector.OK));
        settings.put("gs", gsCommand.getText());
        settings.put("gs-quality", Boolean.valueOf(Autodetector.checkProgram("gs", gsCommand.getText()) == Autodetector.OK));
        
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        latexCommand = new javax.swing.JTextField();
        dvipsCommand = new javax.swing.JTextField();
        gsCommand = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        latexResult = new javax.swing.JLabel();
        dvipsResult = new javax.swing.JLabel();
        gsResult = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        installedLocalesList = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Commands", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        jPanel1.setOpaque(false);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "&latex:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "&dvips:");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "&gs:");

        latexCommand.setText("jTextField1");

        dvipsCommand.setText("jTextField2");

        gsCommand.setText("jTextField3");

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, "&Browse");

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, "B&rowse");

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, "Br&owse");

        org.openide.awt.Mnemonics.setLocalizedText(latexResult, "Working...");

        org.openide.awt.Mnemonics.setLocalizedText(dvipsResult, "Working...");

        org.openide.awt.Mnemonics.setLocalizedText(gsResult, "Working...");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsResult)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(gsCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton3))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(latexCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .add(dvipsCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .add(dvipsResult)
                            .add(latexResult))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jButton2)
                            .add(jButton1))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jButton1)
                    .add(latexCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(latexResult)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jButton2)
                    .add(dvipsCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dvipsResult)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel3)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(gsCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jButton3)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsResult))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dictionaries"));
        installedLocalesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        installedLocalesList.setVisibleRowCount(4);
        jScrollPane1.setViewportView(installedLocalesList);

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, "Add...");

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, "Remove");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jButton5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton5))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField dvipsCommand;
    private javax.swing.JLabel dvipsResult;
    private javax.swing.JTextField gsCommand;
    private javax.swing.JLabel gsResult;
    private javax.swing.JList installedLocalesList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField latexCommand;
    private javax.swing.JLabel latexResult;
    // End of variables declaration//GEN-END:variables
    
    private static final class CheckProgram implements Runnable {
        
        private String type;
        private JTextField command;
        private JLabel result;
        
        public CheckProgram(String type, JTextField command, JLabel result) {
            this.type = type;
            this.command = command;
            this.result = result;
        }
        
        public void run() {
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
        }
        
    }
    
}
