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
/*
 * Iterator.java
 *
 * Created on February 16, 2005, 8:14 PM
 */

package org.netbeans.modules.povproject.wizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.povproject.PovProjectFactory;
import org.netbeans.modules.project.uiapi.ProjectChooserFactory;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Wizard Iterator which provides a single panel for configuring the 
 * project name.
 *
 * @author Timothy Boudreau
 */
public class NewProjectIterator implements TemplateWizard.Iterator{
    String targetName = null;
    private List listeners = new ArrayList();
    
    private static final String KEY_SCENEFILE = "sceneFile";
          
    
    /** Creates a new instance of Iterator */
    private NewProjectIterator() {
    }
    
    public static NewProjectIterator iterator() {
        return new NewProjectIterator();
    }

    public java.util.Set instantiate (final TemplateWizard wiz) throws IOException {
        
        //Get the settings the user entered in the wizard
        String name = (String) wiz.getProperty(
                ProjectChooserFactory.WIZARD_KEY_TARGET_NAME);
        
        DataObject template = wiz.getTemplate();
        DataFolder dest = wiz.getTargetFolder();
        
        String sceneFileName = (String) wiz.getProperty(KEY_SCENEFILE);
        
        //Strip the trailing ".pov" off the name
        if (sceneFileName.endsWith(".pov") && sceneFileName.length() > 4) {
            sceneFileName = sceneFileName.substring(0, sceneFileName.length()-4);
        }
        
        return Collections.singleton(
                PovProjectFactory.createNewPovrayProject (name, template, dest, 
                sceneFileName));
    }


    /** Initializes the iterator after it is constructed.
     * The iterator can for example obtain the {@link #targetChooser target chooser}
     * from the wizard if it does not wish to provide its own.
     * @param wiz template wizard that wishes to use the iterator
     */
    public void initialize(TemplateWizard wiz) {
        targetName = wiz.getTargetName();
        DataFolder tfld = wiz.getTemplatesFolder();
        DataObject template = wiz.getTemplate();
        panel = null;
    }

    /** Informs the Iterator that the TemplateWizard finished using the Iterator.
     * The main purpose of this method is to perform cleanup tasks that should
     * not be left on the garbage collector / default cleanup mechanisms.
     * @param wiz wizard which is no longer being used
     */
    public void uninitialize(TemplateWizard wiz) {
        targetName = null;
        panel = null;
    }  
    
    Panel panel = null;
    
   /** Get the current panel.
    * @return the panel
    */
    public Panel current ()  {
        if (panel == null) {
            panel = new Pnl();
        }
        return panel;
    }


    /** Get the name of the current panel.
    * @return the name
    */
    public String name () {
        return NbBundle.getMessage(NewProjectIterator.class,
                "LBL_NewProjectPanel");
    }

    /** Test whether there is a next panel.
    * @return <code>true</code> if so
    */
    public boolean hasNext () {
        return false;
    }

    /** Test whether there is a previous panel.
    * @return <code>true</code> if so
    */
    public boolean hasPrevious () {
        return false;
    }

    /** Move to the next panel.
    * I.e. increment its index, need not actually change any GUI itself.
    * @exception NoSuchElementException if the panel does not exist
    */
    public void nextPanel () {
        throw new NoSuchElementException();
    }

    /** Move to the previous panel.
    * I.e. decrement its index, need not actually change any GUI itself.
    * @exception NoSuchElementException if the panel does not exist
    */
    public void previousPanel () {
        throw new NoSuchElementException();
    }


    private void fire() {
        ChangeEvent ce = new ChangeEvent(this);
        for (java.util.Iterator i=listeners.iterator();i.hasNext();) {
            ((ChangeListener) i.next()).stateChanged(ce);
        }
    }

    /** Add a listener to changes of the panel's validity.
    * @param l the listener to add
    * @see #isValid
    */
    public void addChangeListener (ChangeListener l) {
        listeners.add (l);
    }

    /** Remove a listener to changes of the panel's validity.
    * @param l the listener to remove
    */
    public void removeChangeListener (ChangeListener l) {
        listeners.remove (l);
    }

    private class Pnl implements WizardDescriptor.Panel, 
                                 DocumentListener, 
                                 ActionListener {
        
        private List listeners = new ArrayList();
        private String targetName = null;
        private JTextField field = null;
        private JTextField mainFile = null;
        private JTextField createIn = null;
        private JPanel panel;
        private JLabel problem;
        private JButton browse = null;
        TemplateWizard settings = null;
        private boolean valid = false;

        
        private File findProjectCreationDir() {
            return ProjectChooser.getProjectsFolder();
        }
        
        private String findUnusedName (File parent, String targetName) {
            return FileUtil.findFreeFolderName(FileUtil.toFileObject(parent), 
                    targetName);
        }
        
        /** Action listener method for the Browse button to pick a destination
         * directory */
        public void actionPerformed (ActionEvent ae) {
            JFileChooser jfc = new JFileChooser (findProjectCreationDir());
            jfc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            jfc.showDialog (panel, NbBundle.getMessage(NewProjectIterator.class, 
                    "LBL_CreateHere"));
            
            if (jfc.getSelectedFile() != null) {
                ProjectChooser.setProjectsFolder(jfc.getSelectedFile());
                change();
            }
        }
        
        public java.awt.Component getComponent () {
             if (panel == null) {
                 final JLabel lbl = new JLabel (NbBundle.getMessage (
                         NewProjectIterator.class, "LBL_FolderName"));
                 problem = new JLabel();
                 problem.setForeground (Color.BLUE);
                 
                 
                 final JLabel mainLbl = new JLabel (NbBundle.getMessage (
                         NewProjectIterator.class, "LBL_MainName"));
                 
                 final JLabel resultName = new JLabel (NbBundle.getMessage (
                         NewProjectIterator.class, "LBL_WillCreate"));
                 
                 field = new JTextField(findUnusedName(findProjectCreationDir(), 
                         "PovrayProject"));
                 
                 mainFile = new JTextField ("Scene");
                 mainLbl.setLabelFor (mainFile);
                 resultName.setLabelFor (createIn);
                 
                 //Note that this could be a JLabel, but to actually make the
                 //component accessible, we need a component you can select
                 //text in
                 
                 createIn = new JTextField (findProjectCreationDir().getPath() + 
                         File.separator + field.getText());
                 
                 createIn.setEditable(false);
                 createIn.setBackground (UIManager.getColor("control"));
                 createIn.setBorder (BorderFactory.createEmptyBorder());
                 createIn.addFocusListener(new FocusAdapter() {
                     public void focusGained (FocusEvent evt) {
                         createIn.selectAll();
                     }
                 });
                 
                 browse = new JButton (NbBundle.getMessage (
                         NewProjectIterator.class, "LBL_Browse"));
                 
                 lbl.setLabelFor (field);
                 panel = new JPanel() {
                     public void doLayout() {
                         //Quick 'n' dirty, why bother with a layout manager?
                         Dimension lblPref = lbl.getPreferredSize();
                         Dimension mainLblPref = mainLbl.getPreferredSize();
                         
                         lblPref.width = Math.max (lblPref.width, 
                                 mainLblPref.width);
                         
                         int end = lblPref.width + 12;
                         int fontBaselineOffset = 2;
                         int gap = 14;
                         lbl.setBounds (12, 12 + fontBaselineOffset, 
                                 lblPref.width + 4, lblPref.height);
                         
                         field.setBounds (end + 4, 12,  getWidth() - (end + 16), 
                                 lblPref.height + 4);
                         
                         int y = 12 + lblPref.height + gap;
                         
                         mainLbl.setBounds (12, y + fontBaselineOffset, 
                                 lblPref.width + 4, lblPref.height);
                         mainFile.setBounds (end + 4, y, getWidth() - (end + 16), 
                                 lblPref.height + 4);
                         
                         y += lblPref.height + gap;
                         
                         resultName.setBounds (12, y, getWidth() - 24, 
                                 lblPref.height + 4);
                         Dimension bPref = browse.getPreferredSize();
                         y += lblPref.height + gap;
                         
                         createIn.setBounds (12, y, 
                                 getWidth() - (24 + bPref.width + gap), 
                                 lblPref.height + 4);
                         browse.setBounds (getWidth() - (12 + bPref.width), y, 
                                 bPref.width, lblPref.height + 4);
                         
                         problem.setBounds (12, getHeight() - lblPref.height, 
                                 getWidth() - 24, lblPref.height);
                     }
                 };
                 panel.add (lbl);
                 panel.add (field);
                 panel.add (mainFile);
                 panel.add (mainLbl);
                 panel.add (resultName);
                 panel.add (createIn);
                 panel.add (browse);
                 panel.setName (NbBundle.getMessage(NewProjectIterator.class, 
                         "CTL_NewProjectPanel"));
                 
                 field.getDocument().addDocumentListener(this);
                 mainFile.getDocument().addDocumentListener(this);
                 browse.addActionListener(this);
                 field.selectAll();
                 panel.add (problem);
                 if (targetName != null) {
                     field.setText(targetName);
                 }
                 change();
             }
             return panel;
         }
         
         public void setProblem (String s) {
             if (problem != null) {
                 problem.setText (s == null ? "" : s);
             }
         }

        public HelpCtx getHelp () {
            return HelpCtx.DEFAULT_HELP;
        }

        public void readSettings (Object settings) {
            System.err.println("Settings is a " + settings);
            targetName = (String) ((TemplateWizard) settings).getProperty(
                    ProjectChooserFactory.WIZARD_KEY_TARGET_NAME);
            
            this.settings = (TemplateWizard) settings;
            try {
                File dir = findProjectCreationDir();
                if (dir != null) {
                    FileObject fob = FileUtil.toFileObject(dir);
                    if (fob != null) {
                        DataFolder fld = (DataFolder) DataObject.find (fob);
                        if (fld != null) {
                            this.settings.setTargetFolder (fld);
                        } else {
                            System.err.println("  No data object for " + dir);
                        }
                    } else {
                        System.err.println("  No fileobject for " + dir);
                    }
                } else {
                    System.err.println(" project creation dir null");
                }
            } catch (DataObjectNotFoundException e) {
                throw new IllegalArgumentException (
                    "Could not find data object for " + 
                     findProjectCreationDir());
            }            
        }

        public void storeSettings (Object settings) {
            if (field != null) {
                ((TemplateWizard) settings).putProperty (
                        ProjectChooserFactory.WIZARD_KEY_TARGET_NAME, 
                        field.getText());
                
                ((TemplateWizard) settings).putProperty (
                        KEY_SCENEFILE,  
                        mainFile.getText());
            }
            this.settings = (TemplateWizard) settings;
        }

        public boolean isValid () {
            return valid;
        }

        private void fire() {
            ChangeEvent ce = new ChangeEvent(this);
            for (java.util.Iterator i=listeners.iterator();i.hasNext();) {
                ((ChangeListener) i.next()).stateChanged(ce);
            }
            NewProjectIterator.this.fire();
        }

        public void addChangeListener (ChangeListener l) {
            listeners.add (l);
        }

        public void removeChangeListener (ChangeListener l) {
            listeners.remove (l);
        }
        
        private void change() {
            if (field != null) {
                String s = field.getText();
                settings.putProperty(
                        ProjectChooserFactory.WIZARD_KEY_TARGET_NAME, s);
                
                settings.putProperty(
                        ProjectChooserFactory.WIZARD_KEY_TARGET_FOLDER, 
                        mainFile.getText());
                try {
                    settings.setTargetFolder((DataFolder) DataObject.find (
                            FileUtil.toFileObject(
                                ProjectChooser.getProjectsFolder())));
                    
                } catch (DataObjectNotFoundException donfe) {
                    //Should never happen
                    ErrorManager.getDefault().notify(donfe);
                }
                setValid( isValidFileName(s) && 
                          isValidFileName(mainFile.getText()) && 
                          checkContainsSpaces(findProjectCreationDir().getPath()));
                
                createIn.setText(findProjectCreationDir().getPath() + 
                        File.separator + s);
            } 
        }
        
        private boolean checkContainsSpaces (String path) {
            if (path.indexOf (' ') != -1) {
                setProblem (NbBundle.getMessage (NewProjectIterator.class, 
                        "PROBLEM_SpaceInPath"));
                return false;
            } else {
                return true;
            }
        }
        
        private void setValid (boolean val) {
            if (valid != val) {
                valid = val;
                fire();
            }
        }
        
        private boolean isValidFileName (String s) {
            if (s.indexOf(File.separator) != -1) {
                setProblem (NbBundle.getMessage (NewProjectIterator.class, 
                        "PROBLEM_BadFileName", s));
                
                return false;
            }
            if (s.indexOf(' ') != -1) {
                //POV-Ray, unfortunately, handles spaces in paths (or shell 
                //quoting) extremely badly, so block this
                setProblem (NbBundle.getMessage (NewProjectIterator.class, 
                        "PROBLEM_SpaceInPath"));
                
                return false;
            }
            if (s.trim().length() == 0) {
                setProblem (NbBundle.getMessage (NewProjectIterator.class, 
                        "PROBLEM_NoFileName"));
                return false;
            }
            try {
                DataFolder fld = (DataFolder) DataObject.find (
                        FileUtil.toFileObject(findProjectCreationDir()));

                if (fld == null) {
                    setProblem (NbBundle.getMessage(NewProjectIterator.class, 
                            "PROBLEM_NoTarget"));
                    return false;
                }
                FileObject obj = fld.getPrimaryFile().getFileObject(s);
                if (obj == null) {
                    setProblem (null);
                } else {
                    setProblem (NbBundle.getMessage(NewProjectIterator.class, 
                            "PROBLEM_FileExists", s));
                }
                return obj == null;
            } catch (Exception e) {
                setProblem (e.getMessage());
                return false;
            }
        }

        //DocumentListener implementation
        public void insertUpdate(DocumentEvent e) {
            change();
        }

        public void removeUpdate(DocumentEvent e) {
            change();
        }

        public void changedUpdate(DocumentEvent e) {
            change();
        }
    }
}

    