/*  The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.
    You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.
    When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.wizard2.demo.filecreation;
import java.awt.Component;
import java.io.File;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.netbeans.spi.wizard.*;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
/**
 *
 * @author  Tim
 */
public class FileLocationPage extends WizardPage implements WizardResultProducer {
    
    /** Creates new form FileLocationPage */
    public FileLocationPage() {
        initComponents();
        setName ("Enter File Name");
    }
    
    protected void renderingPage() {
        /*
        File file = (File) getWizardData("locationFolder"); //NOI18N
        if (file != null) {
            File dir = FileUtil.normalizeFile(file); 
            jTextField2.setText(dir.getPath());
        }
        String nm = (String) getWizardData("name"); //NOI18N
        System.err.println("NAME IS " + nm);
        jTextField4.setText(nm == null ? "" : nm); //NOI18N
         */
    }
    
    public String validateContents(Component c, Object obj) {
        String result = check (jTextField1.getText(), false);
        if (result == null) {
            result = check (jTextField3.getText(), true);
        }
        File fld = new File (jTextField3.getText());
        if (!fld.exists()) {
            return "Folder does not exist";
        }
        if (!fld.isDirectory()) {
            return fld.getName() + " is not a directory";
        }
        return result;
    }
    
    private String check (String s, boolean fld) {
        s = s.trim();
        if (s.length() == 0) {
            return fld ? "Non-existent folder" : "Invalid file name";
        }
        if (!fld && s.indexOf (File.separatorChar) > 0) {
            return "File name may not contain folder separator characters";
        }
        if (!fld && s.indexOf (File.pathSeparatorChar) > 0) {
            return "File name may not contain path separator characters";
        }
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jLabel1.text")); // NOI18N

        jTextField1.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField1.text")); // NOI18N
        jTextField1.setName(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField1.name")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jLabel2.text")); // NOI18N

        jTextField2.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        jTextField2.setEditable(false);
        jTextField2.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jLabel3.text")); // NOI18N

        jTextField3.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField3.text")); // NOI18N
        jTextField3.setName(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField3.name")); // NOI18N

        jButton1.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jButton1.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jLabel4.text")); // NOI18N

        jTextField4.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        jTextField4.setEditable(false);
        jTextField4.setText(org.openide.util.NbBundle.getMessage(FileLocationPage.class, "FileLocationPage.jTextField4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jButton1)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(188, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
    

    public Object finish(Map m) throws WizardException {
        try {
            String folderName = (String) m.get("folder");
            File fld = new File(folderName);

            assert fld.exists();
            String filename = (String) m.get("filename");
            File f = new File(fld, filename);

            assert !f.exists();
            if (!f.createNewFile()) {
                throw new WizardException("Could not create file");
            }
            FileObject ob = FileUtil.toFileObject(f);
            return ob;
        } catch (IOException ex) {
            throw new WizardException (ex.getMessage());
        }
    }

    public boolean cancel(Map arg0) {
        return true;
    }
    
    public static Wizard makeWizard() {
        FileLocationPage pg = new FileLocationPage();
        return WizardPage.createWizard(new WizardPage[] { pg }, pg);
    }
    
    public static String getStep() {
        return "step1";
    }
    
    public static String getDescription() {
        return "Create File";
    }
}