/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package beans2nbm.ui;

import beans2nbm.gen.JarInfo;
import java.awt.Insets;
import java.io.File;
import java.util.Arrays;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author  Tim Boudreau
 */
public class SelectBeansPage extends WizardPage implements JarInfo.ScanObserver, ListDataListener {
    private JarInfo info;
    
    /** Creates new form SelectBeansPage */
    public SelectBeansPage() {
        super (false);
        initComponents();
        jList1.setModel (new DefaultListModel());
        jList2.setModel (new DefaultListModel());
        //Unable to get GBL not to cause jumping when the preferred size
        //changes in one of the lists because something was added...
        setLayout (null);
    }
    
    public void addNotify() {
        super.addNotify();
        String s = (String) getWizardData("jarFileName");
        if (s != null) {
            if (!(new File(s).isFile())) {
                setProblem ("Jar file not specified or not found");
            } else {
                if (info == null || !info.getFileName().equals(s)) {
                    jProgressBar1.setValue (0);
                    jProgressBar1.setVisible(true);
                    info = new JarInfo (s);
                    info.scan (this);
                    super.putWizardData("jarInfo", info);
                }
            }
        }
    }
    
    public void removeNotify() {
        super.removeNotify();
    }
    
    public static String getStep() {
        return "selectBeans";
    }

    public static String getDescription() {
        return "Select JavaBeans";
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        jLabel1.setLabelFor(jList1);
        jLabel1.setText("Classes");
        jLabel1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabel1, gridBagConstraints);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setEnabled(false);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setEnabled(false);
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });

        jScrollPane2.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        jLabel2.setLabelFor(jList2);
        jLabel2.setText("JavaBeans");
        jLabel2.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabel2, gridBagConstraints);

        jButton1.setText("Add ->");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jButton1, gridBagConstraints);

        jButton2.setText("<- Remove");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 22, 5, 22);
        add(jProgressBar1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int[] ixs = jList2.getSelectedIndices();
        Arrays.sort (ixs);
        if (ixs.length > 0) {
            for (int i=ixs.length-1; i >= 0; i--) {
                BeanItem item = (BeanItem) jList2.getModel().getElementAt(ixs[i]);
                BeansListModel mdl = (BeansListModel) jList2.getModel();
                mdl.remove(item);
                
                if (mdl.getSize() == 0) {
                    setProblem ("No JavaBeans selected");
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int[] ixs = jList1.getSelectedIndices();
        if (ixs.length > 0) {
            for (int i=0; i < ixs.length; i++) {
                BeanItem item = (BeanItem) jList1.getModel().getElementAt(ixs[i]);
                BeansListModel mdl = (BeansListModel) jList2.getModel();
                mdl.add(item);
            }
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        int[] ixs = jList2.getSelectedIndices();
        jButton2.setEnabled (ixs.length > 0);
    }//GEN-LAST:event_jList2ValueChanged

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        int[] ixs = jList1.getSelectedIndices();
        if (ixs.length > 0) {
            boolean enableAdd = false;
            for (int i=0; i < ixs.length; i++) {
                Object o = jList1.getModel().getElementAt(ixs[i]);
                if (!(o instanceof BeanItem)) {
                    //Still under construction - call from request focus
                    return;
                }
                BeanItem item = (BeanItem) o;
                if (!info.getBeans().contains(item)) {
                    enableAdd = true;
                    break;
                }
            }
            jButton1.setEnabled(enableAdd);
        } else {
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jList1ValueChanged

    public void start() {
//        System.err.println("Start");
        setBusy(true);
        jProgressBar1.setVisible(true);
        jLabel1.setEnabled(false);
        jLabel2.setEnabled(false);
        jList1.setEnabled(false);
        jList2.setEnabled(false);
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        failed = false;
    }

    public void progress(int progress) {
//        System.err.println("Progress " + progress);
        if (progress != Integer.MIN_VALUE && progress != Integer.MAX_VALUE) {
            jProgressBar1.setValue(progress);
        } else if (progress == Integer.MAX_VALUE) {
            jProgressBar1.setValue(100);
        }
    }

    boolean failed = false;
    public void fail(String msg) {
//        System.err.println("Fail " + msg);
        failed = true;
        setProblem(msg);
    }

    public void done() {
//        System.err.println("Done.");
        BeansListModel beans = new BeansListModel (info);
        EntriesListModel entries = new EntriesListModel (info);
        jList1.setModel(entries);
        jList2.setModel(beans);
        jList1.setEnabled(!failed);
        jList2.setEnabled(!failed);
        jLabel1.setEnabled(!failed);
        jLabel2.setEnabled(!failed);
        jProgressBar1.setVisible(false);
        setBusy(false);
        beans.addListDataListener(this);
        updateCodeName();
        if (jList1.getSelectedIndices().length == 0 && jList1.getModel().getSize() > 0) {
            jList1.setSelectedIndex(0);
        }
        if (beans.getSize() == 0) {
            setProblem ("Add some classes to the list of components");
        }
        doLayout();
    }

    public void intervalAdded(ListDataEvent e) {
        updateCodeName();
    }

    public void intervalRemoved(ListDataEvent e) {
        updateCodeName();
    }

    public void contentsChanged(ListDataEvent e) {
        updateCodeName();
    }
    
    public void requestFocus() {
        if (jList1.getSelectedIndices().length == 0 && jList1.getModel().getSize() > 0) {
            jList1.setSelectedIndex(0);
        }
        jList1.requestFocus();
    }
    
    private void updateCodeName() {
        if (jList2.getModel() instanceof BeansListModel) {
            String s = ((BeansListModel) jList2.getModel()).getLikelyCodeName();
//            System.err.println("Most likely code name " + s);
            if (s != null) {
                putWizardData("codename", s);
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables


    boolean isAqua = "Aqua".equals (UIManager.getLookAndFeel().getID());
    int SPACE = isAqua ? 12 : 5;
    public void doLayout() {
        //Bad, bad gridbag!  Do it the old fashioned way...
        int buttonPreferredSize = Math.max (jButton1.getPreferredSize().width,
                jButton2.getPreferredSize().width);
        Insets ins = getInsets();
        int workingWidth = getWidth() - (ins.left + ins.right);

        int x = ins.left;
        int y = ins.top;

        int workingHeight = getHeight() - (ins.top + ins.bottom);

        int buttonGap = buttonPreferredSize + (SPACE * 2);
        int listWidth = (workingWidth - buttonGap) / 2;

        int lblHeight = Math.max (jLabel1.getPreferredSize().height, jLabel2.getPreferredSize().height);
        int listsY = y + lblHeight + SPACE;

        if (jProgressBar1.isVisible()) {
            int h = jProgressBar1.getPreferredSize().height;
            jProgressBar1.setBounds (x, (getHeight() / 2) - (h / 2), workingWidth, h);
            jScrollPane1.setBounds (0,0,0,0);
            jScrollPane2.setBounds (0,0,0,0);
            jButton1.setBounds (0,0,0,0);
            jButton2.setBounds (0,0,0,0);
            jLabel1.setBounds (0,0,0,0);
            jLabel2.setBounds (0,0,0,0);
            return;
        }

        jScrollPane1.setBounds(x, listsY, listWidth, workingHeight - listsY);
        jScrollPane2.setBounds(x + listWidth + buttonGap, listsY, listWidth, workingHeight - listsY);


        jLabel1.setBounds (x, y, jLabel1.getPreferredSize().width, lblHeight);
        jLabel2.setBounds (x + listWidth + buttonGap, y, jLabel2.getPreferredSize().width, lblHeight);

        int button1height = jButton1.getPreferredSize().height;
        jButton1.setBounds(x + listWidth + SPACE, listsY, buttonPreferredSize, button1height);
        jButton2.setBounds(x + listWidth + SPACE, listsY + button1height + SPACE, buttonPreferredSize, jButton2.getPreferredSize().height);
    }
}
