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

package org.netbeans.modules.groovy.groovyproject;

import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.groovy.groovyproject.ui.customizer.MainScriptWarning;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.groovy.support.api.GroovySettings;
import org.openide.util.Exceptions;

/** 
 * Action provider of the Groovy project. This is the place where to do
 * strange things to Groovy actions. E.g. compile-single.
 */
class GroovyActionProvider implements ActionProvider {
    
    // Commands available from Groovy project
    private static final String[] supportedActions = {
        COMMAND_BUILD, 
        COMMAND_CLEAN, 
        COMMAND_REBUILD, 
        COMMAND_COMPILE_SINGLE, 
        COMMAND_RUN, 
        COMMAND_RUN_SINGLE, 
    };
    
    // Project
    GroovyProject project;
    
    // Ant project helper of the project
    private AntProjectHelper antProjectHelper;
    
        
    /** Map from commands to ant targets */
    Map/*<String,String[]>*/ commands;
    
    public GroovyActionProvider( GroovyProject project, AntProjectHelper antProjectHelper ) {
        
        commands = new HashMap();
            commands.put(COMMAND_BUILD, new String[] {"jar"}); // NOI18N
            commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
            commands.put(COMMAND_REBUILD, new String[] {"clean", "jar"}); // NOI18N
            commands.put(COMMAND_COMPILE_SINGLE, new String[] {"compile-single"}); // NOI18N
            commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
            commands.put(COMMAND_RUN_SINGLE, new String[] {"run-single"}); // NOI18N
        
        this.antProjectHelper = antProjectHelper;
        this.project = project;
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(GeneratedFilesHelper.BUILD_XML_PATH);
    }
    
    public String[] getSupportedActions() {
        return supportedActions;
    }
    
    public void invokeAction( String command, Lookup context ) throws IllegalArgumentException {
        Properties p = new Properties();
        String[] targetNames;
        
        targetNames = getTargetNames(command, context, p);
        if (targetNames == null) {
            return;
        }
        if (targetNames.length == 0) {
            targetNames = null;
        }
        if (p.keySet().size() == 0) {
            p = null;
        }
        
        String homeString = null;
        if (p != null) {
            homeString = (String)p.get ("groovy.home"); // NOI18N
        }
        if (homeString == null) {
            String msg = NbBundle.getMessage(GroovyActionProvider.class, "MSG_HomeNotSet" );
            DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message(msg,
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
            return;
        }
        File home = new File(homeString);
        if (!home.exists()) {
            String msg = NbBundle.getMessage(GroovyActionProvider.class, "MSG_HomeInvalid", home.getPath() );
            DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message(msg,
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
            return;
        }
        try {
            ActionUtils.runTarget(findBuildXml(), targetNames, p);
        } 
        catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    /**
     * @return array of targets or null to stop execution; can return empty array
     */
    /*private*/ String[] getTargetNames(String command, Lookup context, Properties p) throws IllegalArgumentException {
        GroovySettings groovyOption = new GroovySettings();
        File gh = new File(groovyOption.getGroovyHome());
        if (gh != null) {
            p.setProperty("groovy.home", gh.getAbsolutePath());
        }
        String[] targetNames = new String[0];
        if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            FileObject[] files = findSourcesAndPackages( context, project.getSourceDirectory());
            if (files != null) {
                p.setProperty("groovyc.includes", ActionUtils.antIncludesList(files, project.getSourceDirectory())); // NOI18N
                targetNames = new String[] {"compile-single"}; // NOI18N
            } 
        } 
        else if (command.equals (COMMAND_RUN)) {
            EditableProperties ep = antProjectHelper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);

            // check project's main script
            String mainScript = (String)ep.get ("main.script"); // NOI18N
            
            while (!isSetMainScript (GroovyProjectUtil.getProjectSourceDirectory (project), mainScript)) {
                // show warning, if cancel then return
                if (showMainScriptWarning (mainScript, ProjectUtils.getInformation(project).getDisplayName(), ep)) {
                    return null;
                }
                mainScript = (String)ep.get ("main.script"); // NOI18N
                antProjectHelper.putProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
            }

            p.setProperty("main.script", mainScript + ".groovy"); // NOI18N
            if (!command.equals(COMMAND_RUN)) {
                p.setProperty("debug.class", mainScript); // NOI18N
            }
            
            targetNames = (String[])commands.get(command);
            if (targetNames == null) {
                throw new IllegalArgumentException(command);
            }
        } else if (command.equals (COMMAND_RUN_SINGLE)) {
            FileObject fos[] = findSources(context);
            if (fos == null || fos.length != 1) {
                return null;
            }
            FileObject file = fos[0];
            String script = FileUtil.getRelativePath(project.getSourceDirectory(), file);
            p.setProperty("groovyc.includes", script); // NOI18N
            if (command.equals (COMMAND_RUN_SINGLE)) {
                p.setProperty("run.script", script); // NOI18N
                targetNames = (String[])commands.get(COMMAND_RUN_SINGLE);
            }
        } else {
            targetNames = (String[])commands.get(command);
            if (targetNames == null) {
                throw new IllegalArgumentException(command);
            }
        }
        return targetNames;
    }
    
    public boolean isActionEnabled( String command, Lookup context ) {
        if ( findBuildXml() == null ) {
            return false;
        }
        if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            return findSourcesAndPackages( context, project.getSourceDirectory()) != null;
        }
        else if (command.equals(COMMAND_RUN_SINGLE)) {
            FileObject fos[] = findSources(context);
            return (fos != null && fos.length == 1);
        }
        // other actions are global
        return true;
    }
    
    // Private methods -----------------------------------------------------
    
    /** Find selected sources 
     */
    private FileObject[] findSources(Lookup context) {
        FileObject srcDir = project.getSourceDirectory();
        if (srcDir != null) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcDir, ".groovy", true); // NOI18N
            return files;
        } else {
            return null;
        }
    }

    private FileObject[] findSourcesAndPackages (Lookup context, FileObject srcDir) {
        if (srcDir != null) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcDir, null, true); // NOI18N
            //Check if files are either packages of java files
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].isFolder() && !"groovy".equals(files[i].getExt())) {
                        return null;
                    }
                }
            }
            return files;
        } else {
            return null;
        }
    }

    private boolean isSetMainScript (FileObject sourcesRoot, String mainScript) {
        if (mainScript == null || mainScript.length () == 0) {
            return false;
        }
        
        return GroovyProjectUtil.isMainScript (mainScript, sourcesRoot);
    }
    
    private boolean showMainScriptWarning (String mainScript, String projectName, EditableProperties ep) {
        boolean canceled;
        final JButton okButton = new JButton (NbBundle.getMessage (GroovyActionProvider.class, "LBL_MainScriptWarning_ChooseMainScript_OK")); // NOI18N
        
        // main script goes wrong => warning
        final MainScriptWarning panel = new MainScriptWarning (ProjectUtils.getInformation(project).getDisplayName(), project.getSourceDirectory ());

        Object[] options = new Object[] {
            okButton,
            DialogDescriptor.CANCEL_OPTION
        };
        
        panel.addChangeListener (new ChangeListener () {
           public void stateChanged (ChangeEvent e) {
               if (e.getSource () instanceof MouseEvent && MouseUtils.isDoubleClick (((MouseEvent)e.getSource ()))) {
                   // click button and the finish dialog with selected class
                   okButton.doClick ();
               } else {
                   okButton.setEnabled (panel.getSelectedMainScript () != null);
               }
           }
        });
        
        okButton.setEnabled (false);
        DialogDescriptor desc = new DialogDescriptor (panel,
            NbBundle.getMessage (GroovyActionProvider.class, "CTL_MainScriptWarning_Title", ProjectUtils.getInformation(project).getDisplayName()), // NOI18N
            true, options, options[0], DialogDescriptor.BOTTOM_ALIGN, null, null);
        desc.setMessageType (DialogDescriptor.INFORMATION_MESSAGE);
        Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.setVisible (true);
        if (desc.getValue() != options[0]) {
            canceled = true;
        } else {
            mainScript = panel.getSelectedMainScript ();
            canceled = false;
            ep.put ("main.script", mainScript == null ? "" : mainScript); // NOI18N
        }
        dlg.dispose();            

        return canceled;
    }

}
