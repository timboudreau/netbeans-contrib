/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.PlatformsCustomizer;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.modules.groovy.groovyproject.GroovyProjectUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerGeneral extends JPanel implements GroovyCustomizer.Panel, HelpCtx.Provider {
    
    private GroovyProjectProperties groovyProperties;
    private VisualPropertySupport vps;
    
    /** Creates new form CustomizerCompile */
    public CustomizerGeneral( GroovyProjectProperties groovyProperties ) {
        initComponents();        
        this.groovyProperties = groovyProperties;
        this.putClientProperty( "HelpID", "Groovy_CustomizerGeneral" ); // NOI18N
        vps = new VisualPropertySupport( groovyProperties );
    }
    
    
    public void initValues() {
        
        //vps.register( jTextFieldDisplayName, GroovyProjectProperties.GROOVY_PROJECT_NAME );
        FileObject projectFolder = groovyProperties.getProject().getProjectDirectory();
        File pf = FileUtil.toFile( projectFolder );
        jTextFieldFolder.setText( pf == null ? "" : pf.getPath() ); // NOI18N
        initPlatforms(vps);
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( CustomizerGeneral.class );
    }
        
    private void initPlatforms(VisualPropertySupport vps) {
        Object value = groovyProperties.get (GroovyProjectProperties.JAVA_PLATFORM);
        String activePlatform = (String) GroovyProjectUtil.getEvaluatedProperty(groovyProperties.getProject(), (String) value);
        boolean activeFound = false;
        String activeName = activePlatform;
        // Read defined platforms
        JavaPlatform[] platforms = JavaPlatformManager.getDefault().getInstalledPlatforms();
        List platformNames = new ArrayList ();
        for( int i = 0; i < platforms.length; i++ ) {
            Specification spec = platforms[i].getSpecification();
            //Show only nonbroken j2se platforms
            if ("j2se".equalsIgnoreCase (spec.getName()) && platforms[i].getInstallFolders().size()>0) { // NOI18N
                platformNames.add(platforms[i].getDisplayName());
                String name = platforms[i].getDisplayName();
                if (name.equals(activePlatform)) {
                    activeFound = true;
                    activeName = platforms[i].getDisplayName();
                }
            }
        }
        if (!activeFound) {
            // Active platform was not found in the list of platforms.
            // Perhaps this is project with broken references? Add it to combo even if the platform is broken
            // to let the user to correct it
            platformNames.add(activePlatform);
        }
        vps.register( jComboBoxTarget, (String[])platformNames.toArray(new String[platformNames.size()]),
                GroovyProjectProperties.JAVA_PLATFORM, activeName);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelProjectFolder = new javax.swing.JLabel();
        jTextFieldFolder = new javax.swing.JTextField();
        jLabelTarget = new javax.swing.JLabel();
        jComboBoxTarget = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EtchedBorder());
        jLabelProjectFolder.setLabelFor(jTextFieldFolder);
        jLabelProjectFolder.setText(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "LBL_CustomizeGeneral_ProjectFolder_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jLabelProjectFolder, gridBagConstraints);
        jLabelProjectFolder.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "ACSD_CustomizerGeneral_jLabelProjectFolder"));

        jTextFieldFolder.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        add(jTextFieldFolder, gridBagConstraints);

        jLabelTarget.setLabelFor(jComboBoxTarget);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelTarget, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "LBL_CustomizeGeneral_Platform_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        add(jLabelTarget, gridBagConstraints);
        jLabelTarget.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "ACSD_CustomizerGeneral_jLabelTarget"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jComboBoxTarget, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "LBL_CustomizeGeneral_Platform_JButton"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewPlatform(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 12);
        add(jButton1, gridBagConstraints);
        jButton1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "ACSD_CustomizerGeneral_jButton1"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents

    private void createNewPlatform(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewPlatform
        PlatformsCustomizer.showCustomizer(null);
        initPlatforms (vps);
    }//GEN-LAST:event_createNewPlatform
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBoxTarget;
    private javax.swing.JLabel jLabelProjectFolder;
    private javax.swing.JLabel jLabelTarget;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldFolder;
    // End of variables declaration//GEN-END:variables
        
    // Storing methods ---------------------------------------------------------
    
    /** Stores the value according to the src component into the helper
     */
    private void store( JComponent src ) {
    
        /*
        if ( src == jTextFieldSrcDir ) {
            groovyProperties.put( J2SEProjectProperties.SRC_DIR, jTextFieldSrcDir.getText() );
        }
        else if ( src == jTextFieldBuildDir ) {
            groovyProperties.put( J2SEProjectProperties.BUILD_DIR, jTextFieldBuildDir.getText() );
        }
        else if ( src == jListClasspath ) {
            
            List elements = new ArrayList( classpathModel.size() );
            
            for ( Enumeration e = classpathModel.elements(); e.hasMoreElements(); ) {
                elements.add( e.nextElement() );
            }
            groovyProperties.put( J2SEProjectProperties.JAVAC_CLASSPATH, elements );
        }
        
        assert true : "CustomizerCompile - Unknown component : " + src; // NOI18N
        */
    } 
    
    
    
    // Private methods for classpath data manipulation -------------------------
        
}
