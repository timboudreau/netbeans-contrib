/*
 * NewGrailsProjectPanel.java
 *
 * Created on October 1, 2007, 2:49 PM
 */

package org.netbeans.modules.groovy.grailsproject.ui.wizards;

import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.NbBundle;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.groovy.grailsproject.GrailsProject;
import org.netbeans.modules.groovy.grailsproject.SourceCategory;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author  schmidtm
 */
public class GetArtifactNamePanel extends WizardSettingsPanel implements DocumentListener {
    GetArtifactNameStep parentStep;
    GrailsProject project;
    String baseDir;
    private String fileName = "";
    String suffix = "";
    
    SourceCategory cat;
        
    boolean valid(WizardDescriptor settings) {
        
            if(classNameTextField.getText().length() > 0)
                return true;
            
            return false;
        }
    
    void read (WizardDescriptor d) {

    }
    
    void validate (WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    void store( WizardDescriptor d ) {
        
        d.putProperty( "projectFolder", projectTextField.getText() ); // NOI18N
        parentStep.fireChangeEvent();
        }
    
    
    
    /** Creates new form NewGrailsProjectPanel */
    public GetArtifactNamePanel(GetArtifactNameStep parentStep, SourceCategory cat) {
        this.parentStep = parentStep;
        this.cat = cat;
        
        initComponents();

        String subDirName = "<unknown>";
        
        switch(cat){
            case DOMAIN:
                setName(NbBundle.getMessage(GetArtifactNamePanel.class,"WIZARD_TITLE_DOMAIN")); // NOI18N
                subDirName = "domain";
                break;
            case CONTROLLERS:
                setName(NbBundle.getMessage(GetArtifactNamePanel.class,"WIZARD_TITLE_CONTROLLERS")); // NOI18N
                subDirName = "controllers";
                suffix = "Controller";
                break;
            case SERVICES:
                setName(NbBundle.getMessage(GetArtifactNamePanel.class,"WIZARD_TITLE_SERVICES")); // NOI18N
                subDirName = "services";
                suffix = "Service";
                break; 
            case VIEWS:
                setName(NbBundle.getMessage(GetArtifactNamePanel.class,"WIZARD_TITLE_VIEWS")); // NOI18N
                subDirName = "views";
                break;    
            case TAGLIB:
                setName(NbBundle.getMessage(GetArtifactNamePanel.class,"WIZARD_TITLE_TAGLIB")); // NOI18N
                subDirName = "taglib";
                break;    
            }
        
        // populate the panel with some stuff
        
        project = parentStep.getGrailsProject();

        projectTextField.setText(project.getProjectDirectory().getName());
        
        baseDir =   FileUtil.getFileDisplayName(project.getProjectDirectory()) + 
                    File.separatorChar + "grails-app" + File.separatorChar + subDirName;
        
        createdFileTextField.setText(baseDir + File.separatorChar );
        
        // register event listeners to auto-update some fields.

        classNameTextField.getDocument().addDocumentListener( this );
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        classNameLabel = new javax.swing.JLabel();
        projectLabel = new javax.swing.JLabel();
        classNameTextField = new javax.swing.JTextField();
        projectTextField = new javax.swing.JTextField();
        createdFileLabel = new javax.swing.JLabel();
        createdFileTextField = new javax.swing.JTextField();

        classNameLabel.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetDomainClassNamePanel.projectNameLabel.text")); // NOI18N

        projectLabel.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetDomainClassNamePanel.projectFolderLabel.text")); // NOI18N

        classNameTextField.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetDomainClassNamePanel.projectNameTextField.text")); // NOI18N

        projectTextField.setEditable(false);
        projectTextField.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetArtifactNamePanel.projectTextField.text")); // NOI18N

        createdFileLabel.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetArtifactNamePanel.createdFileLabel.text")); // NOI18N

        createdFileTextField.setEditable(false);
        createdFileTextField.setText(org.openide.util.NbBundle.getMessage(GetArtifactNamePanel.class, "GetArtifactNamePanel.createdFileTextField.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(classNameLabel)
                    .add(createdFileLabel)
                    .add(projectLabel))
                .add(23, 23, 23)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createdFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .add(classNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(classNameLabel)
                    .add(classNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(projectLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createdFileLabel)
                    .add(createdFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classNameLabel;
    private javax.swing.JTextField classNameTextField;
    private javax.swing.JLabel createdFileLabel;
    private javax.swing.JTextField createdFileTextField;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    // End of variables declaration//GEN-END:variables

    
    public void insertUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts( e ) ;
    }
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts( DocumentEvent e ) {
        
        Document doc = e.getDocument();
                
        if ( doc == classNameTextField.getDocument() ) {
            
            fileName = baseDir + File.separatorChar + classNameTextField.getText() + suffix + ".groovy";
            createdFileTextField.setText(fileName);
            projectTextField.setText(project.getProjectDirectory().getName());
            
            parentStep.fireChangeEvent();
            
        }                
  
    }

    public String getDomainClassName(){
        return classNameTextField.getText();
        }

    public void setArtifactName(String text){
        classNameTextField.setText(text);
        parentStep.fireChangeEvent();
    }

    public String getFileName() {
        return fileName;
    }
    
}
