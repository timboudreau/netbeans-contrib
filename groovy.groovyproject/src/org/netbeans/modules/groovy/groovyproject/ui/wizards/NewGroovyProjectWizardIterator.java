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

package org.netbeans.modules.groovy.groovyproject.ui.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.groovy.groovyproject.GroovyProjectGenerator;
import org.netbeans.modules.groovy.groovyproject.ui.FoldersListSettings;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


/**
 * Wizard to create a new J2SE project.
 */
public class NewGroovyProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

    static final int TYPE_APP = 0;
    static final int TYPE_LIB = 1;
    
    static final String PROP_NAME_INDEX = "nameIndex";      //NOI18N

    private static final String MANIFEST_FILE = "manifest.mf"; // NOI18N

    private static final long serialVersionUID = 1L;
    
    private int type;
    
    /** Create a new wizard iterator. */
    public NewGroovyProjectWizardIterator() {
        this(TYPE_APP);
    }
    
    public NewGroovyProjectWizardIterator(int type) {
        this.type = type;
    }
        
    public static NewGroovyProjectWizardIterator library() {
        return new NewGroovyProjectWizardIterator( TYPE_LIB );
    }
    
    private WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {
                new PanelConfigureProject( this.type )
            };
    }
    
    private String[] createSteps() {
            return new String[] {
                NbBundle.getMessage(NewGroovyProjectWizardIterator.class,"LAB_ConfigureProject"), 
            };
    }
    
    
    public Set/*<FileObject>*/ instantiate () throws IOException {
        Set resultSet = new HashSet ();
        File dirF = (File)wiz.getProperty("projdir");        //NOI18N
        if (dirF != null) {
            dirF = FileUtil.normalizeFile(dirF);
        }
        String name = (String)wiz.getProperty("name");        //NOI18N
        String mainScript = (String)wiz.getProperty("mainScript");        //NOI18N
        AntProjectHelper h = GroovyProjectGenerator.createProject(dirF, name, mainScript, type == TYPE_APP ? MANIFEST_FILE : null);
        if (mainScript != null && mainScript.length () > 0) {
            try {
                //String sourceRoot = "src"; //(String)groovyProperties.get (J2SEProjectProperties.SRC_DIR);
                FileObject sourcesRoot = h.getProjectDirectory ().getFileObject ("src");        //NOI18N
                FileObject mainScriptFo = getMainScriptFO (sourcesRoot, mainScript);
                assert mainScriptFo != null : "sourcesRoot: " + sourcesRoot + ", mainScript: " + mainScript;        //NOI18N
                // Returning FileObject of main script, will be called its preferred action
                resultSet.add (mainScriptFo);
            } catch (Exception x) {
                ErrorManager.getDefault().notify(x);
            }
        }
        if ( type == TYPE_LIB ) {
            resultSet.add( h.getProjectDirectory ().getFileObject ("src") );        //NOI18N 
        }
        FileObject dir = FileUtil.toFileObject(dirF);
        if (type == TYPE_APP) {
            createManifest(dir, MANIFEST_FILE);
        }
        Project p = ProjectManager.getDefault().findProject(dir);
        
        // Returning FileObject of project diretory. 
        // Project will be open and set as main
        Integer index = (Integer) wiz.getProperty(PROP_NAME_INDEX);
        switch (this.type) {
            case TYPE_APP:
                FoldersListSettings.getDefault().setNewApplicationCount(index.intValue());
                break;
            case TYPE_LIB:
                FoldersListSettings.getDefault().setNewLibraryCount(index.intValue());
                break;
        }        
        resultSet.add (dir);

        dirF = (dirF != null) ? dirF.getParentFile() : null;
        if (dirF != null && dirF.exists()) {
            ProjectChooser.setProjectsFolder (dirF);    
        }
                        
        return resultSet;
    }
    
        
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent)c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }
    }
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null);           //NOI18N
        this.wiz.putProperty("name",null);          //NOI18N
        this.wiz.putProperty("mainScript",null);         //NOI18N
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format (NbBundle.getMessage(NewGroovyProjectWizardIterator.class,"LAB_IteratorName"),
            new Object[] {new Integer (index + 1), new Integer (panels.length) });                                
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    public boolean hasPrevious() {
        return index > 0;
    }
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    public void previousPanel() {
        if (!hasPrevious()) throw new NoSuchElementException();
        index--;
    }
    public WizardDescriptor.Panel current () {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
    
    // helper methods, finds mainclass's FileObject
    private FileObject getMainScriptFO (FileObject sourcesRoot, String mainScript) {
        // ignore unvalid mainScript ???
        mainScript = mainScript.replace(".", "/");
        return sourcesRoot.getFileObject(mainScript + ".groovy"); // NOI18N
    }

    static String getPackageName (String displayName) {
        StringBuffer builder = new StringBuffer ();
        boolean firstLetter = true;
        for (int i=0; i< displayName.length(); i++) {
            char c = displayName.charAt(i);            
            if ((!firstLetter && Character.isJavaIdentifierPart (c)) || (firstLetter && Character.isJavaIdentifierStart(c))) {
                firstLetter = false;
                if (Character.isUpperCase(c)) {
                    c = Character.toLowerCase(c);
                }                    
                builder.append(c);
            }            
        }
        return builder.length() == 0 ? NbBundle.getMessage(NewGroovyProjectWizardIterator.class,"TXT_DefaultPackageName") : builder.toString();
    }
    
    /**
     * Create a new application manifest file with minimal initial contents.
     * @param dir the directory to create it in
     * @param path the relative path of the file
     * @throws IOException in case of problems
     */
    private static void createManifest(FileObject dir, String path) throws IOException {
        FileObject manifest = dir.createData(MANIFEST_FILE);
        FileLock lock = manifest.lock();
        try {
            OutputStream os = manifest.getOutputStream(lock);
            try {
                PrintWriter pw = new PrintWriter(os);
                pw.println("Manifest-Version: 1.0"); // NOI18N
                pw.println("X-COMMENT: Main-Class will be added automatically by build"); // NOI18N
                pw.println(); // safest to end in \n\n due to JRE parsing bug
                pw.flush();
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
}
